package utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Générateur de PDF minimal, écrit directement au format PDF 1.4
 * (sans dépendre d'une librairie tierce comme Apache PDFBox ou iText).
 *
 * Suffisant pour produire des documents texte simples (procès-verbaux,
 * rapports) : titres, paragraphes, lignes séparatrices, pagination
 * automatique. Le fichier produit est un vrai PDF, ouvrable par
 * n'importe quel lecteur (Adobe Reader, navigateur, etc.).
 */
public class PdfDocument {

    private static final Charset ENCODAGE_TEXTE = Charset.forName("Cp1252");
    private static final double LARGEUR_PAGE = 595;  // A4 en points (1/72 pouce)
    private static final double HAUTEUR_PAGE = 842;
    private static final double MARGE_GAUCHE = 50;
    private static final double Y_DEPART = 792;
    private static final double MARGE_BAS = 50;

    private final List<ByteArrayOutputStream> pages = new ArrayList<>();
    private ByteArrayOutputStream pageCourante;
    private double curseurY;

    public PdfDocument() {
        nouvellePage();
    }

    private void nouvellePage() {
        pageCourante = new ByteArrayOutputStream();
        pages.add(pageCourante);
        curseurY = Y_DEPART;
    }

    private void ecrireAscii(ByteArrayOutputStream out, String texte) {
        try {
            out.write(texte.getBytes("ISO-8859-1"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String echapper(String texte) {
        return texte.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    /** Ajoute une ligne de texte et avance le curseur vertical. Change de page si besoin. */
    public void ajouterTexte(String texte, double taille, boolean gras) {
        if (curseurY < MARGE_BAS) {
            nouvellePage();
        }
        try {
            ecrireAscii(pageCourante, "BT\n");
            ecrireAscii(pageCourante, "/" + (gras ? "F2" : "F1") + " " + taille + " Tf\n");
            ecrireAscii(pageCourante, MARGE_GAUCHE + " " + curseurY + " Td\n");
            pageCourante.write('(');
            pageCourante.write(echapper(texte).getBytes(ENCODAGE_TEXTE));
            pageCourante.write(')');
            ecrireAscii(pageCourante, " Tj\n");
            ecrireAscii(pageCourante, "ET\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        curseurY -= (taille + 6);
    }

    /** Ajoute un petit espace vertical, sans texte. */
    public void ligneVide() {
        curseurY -= 10;
    }

    /** Trace une ligne horizontale fine (séparateur visuel). */
    public void ligneSeparatrice() {
        if (curseurY < MARGE_BAS) {
            nouvellePage();
        }
        ecrireAscii(pageCourante, "0.5 w\n");
        ecrireAscii(pageCourante, MARGE_GAUCHE + " " + curseurY + " m\n");
        ecrireAscii(pageCourante, (LARGEUR_PAGE - MARGE_GAUCHE) + " " + curseurY + " l\n");
        ecrireAscii(pageCourante, "S\n");
        curseurY -= 14;
    }

    /** Écrit le document PDF final sur disque. */
    public void enregistrer(String cheminFichier) throws IOException {
        int nbPages = pages.size();
        int totalObjets = 4 + nbPages * 2;
        // Numérotation : 1=Catalog, 2=Pages, 3=Police normale, 4=Police grasse,
        // puis pour chaque page : (objet Page, objet Contenu) consécutifs.

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        ecrireAscii(pdf, "%PDF-1.4\n");

        offsets.add(pdf.size());
        ecrireAscii(pdf, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets.add(pdf.size());
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < nbPages; i++) {
            kids.append(5 + i * 2).append(" 0 R ");
        }
        ecrireAscii(pdf, "2 0 obj\n<< /Type /Pages /Kids [" + kids.toString().trim()
                + "] /Count " + nbPages + " >>\nendobj\n");

        offsets.add(pdf.size());
        ecrireAscii(pdf, "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica "
                + "/Encoding /WinAnsiEncoding >>\nendobj\n");

        offsets.add(pdf.size());
        ecrireAscii(pdf, "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold "
                + "/Encoding /WinAnsiEncoding >>\nendobj\n");

        for (int i = 0; i < nbPages; i++) {
            int objPage = 5 + i * 2;
            int objContenu = 6 + i * 2;

            offsets.add(pdf.size());
            ecrireAscii(pdf, objPage + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 "
                    + (int) LARGEUR_PAGE + " " + (int) HAUTEUR_PAGE + "] "
                    + "/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> "
                    + "/Contents " + objContenu + " 0 R >>\nendobj\n");

            byte[] contenu = pages.get(i).toByteArray();
            offsets.add(pdf.size());
            ecrireAscii(pdf, objContenu + " 0 obj\n<< /Length " + contenu.length + " >>\nstream\n");
            pdf.write(contenu);
            ecrireAscii(pdf, "\nendstream\nendobj\n");
        }

        int debutXref = pdf.size();
        ecrireAscii(pdf, "xref\n0 " + (totalObjets + 1) + "\n");
        ecrireAscii(pdf, "0000000000 65535 f \n");
        for (int off : offsets) {
            ecrireAscii(pdf, String.format("%010d 00000 n \n", off));
        }
        ecrireAscii(pdf, "trailer\n<< /Size " + (totalObjets + 1) + " /Root 1 0 R >>\n"
                + "startxref\n" + debutXref + "\n%%EOF");

        try (FileOutputStream fos = new FileOutputStream(cheminFichier)) {
            pdf.writeTo(fos);
        }
    }
}
