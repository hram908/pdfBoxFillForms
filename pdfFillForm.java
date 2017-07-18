/**
 * Created by yesuserahailu on 7/17/17.
 */
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.text.PDFTextStripper;

public class pdfFillForm {
    public void printFields(PDDocument pdfDocument) throws IOException
    {
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        List<PDField> fields = acroForm.getFields();
        System.out.println(fields.size() + " top-level fields were found on the form");
        for (PDField field : fields)
            {
                processField(field, "|--", field.getPartialName());
            }
    }

    private void processField(PDField field, String sLevel, String sParent) throws IOException
    {
        String partialName = field.getPartialName();
        if (field instanceof PDNonTerminalField)
            {
                if (!sParent.equals(field.getPartialName()))
                    {
                        if (partialName != null)
                            {
                                sParent = sParent + "." + partialName;
                            }
                    }
                System.out.println(sLevel + sParent);

                for (PDField child : ((PDNonTerminalField)field).getChildren())
                    {
                        processField(child, "|  " + sLevel, sParent);
                    }
            }
            else
            {
                String fieldValue = field.getValueAsString();
                StringBuilder outputString = new StringBuilder(sLevel);
                outputString.append(sParent);
                if (partialName != null)
                    {
                        outputString.append(".").append(partialName);
                    }
                outputString.append(" = ").append(fieldValue);
                outputString.append(",  type=").append(field.getClass().getName());
                System.out.println(outputString);
            }
    }



    public static void main(String [] args) throws IOException {
        File file = new File("/Users/yesuserahailu/Desktop/sample.pdf");

        PDDocument pdfDocument = PDDocument.load(file);
        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();

        //Retrieving text from PDF document
        String text = pdfStripper.getText(pdfDocument);
        System.out.println(pdfDocument.getNumberOfPages());
        System.out.println(text);

        //Set fields based on fields given
        PDAcroForm acroForms = pdfDocument.getDocumentCatalog().getAcroForm();
        if (acroForms != null)
        {
            PDTextField field = (PDTextField) acroForms.getField( "Given Name Text Box" );
            field.setValue("Yesusera Hailu");
            //field = (PDTextField) acroForms.getField( "fieldsContainer.nestedS" );
            //field.setValue("Yesusera");
        }
        pdfDocument.save("/Users/yesuserahailu/Desktop/filledOutPDF.pdf");
        //Prints out fields PDF Document
        pdfFillForm exporter = new pdfFillForm();
        exporter.printFields(pdfDocument);
        pdfDocument.close();
    }
}
