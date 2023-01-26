/*
    Copyright (c) 2023 iText Group NV

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.
 */
package com.itextpdf.pdfdeserializer;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FunctionalTest {

    @Test
    public void layersTest() throws IOException {
        getDocumentWithAllDFields();
        getDocumentUsingDeserializer();
    }

    private void getDocumentUsingDeserializer() throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream("target/OCG-deserialize.pdf")) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                Deserializer deserializer = new Deserializer();
                DeserializationContext context = new DeserializationContext().setPdfDocument(fromDocument);

                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                // Order
                PdfLayer parent1 = new PdfLayer("Parent1", fromDocument);
                PdfLayer child1 = new PdfLayer("Child1", fromDocument);
                pdfResource.addProperties(child1.getPdfObject());
                parent1.addChild(child1);

                // Locked
                PdfLayer locked1 = new PdfLayer("Locked1", fromDocument);
                locked1.setLocked(true);
                pdfResource.addProperties(locked1.getPdfObject());
                PdfLayer locked2 = new PdfLayer("Locked2", fromDocument);
                locked2.setLocked(true);

                // RBGroups
                PdfLayer radio1 = new PdfLayer("Radio1", fromDocument);
                pdfResource.addProperties(radio1.getPdfObject());
                PdfLayer radio2 = new PdfLayer("Radio2", fromDocument);
                PdfLayer radio3 = new PdfLayer("Radio3", fromDocument);
                pdfResource.addProperties(radio3.getPdfObject());
                List<PdfLayer> options = new ArrayList<>();
                options.add(radio1);
                options.add(radio2);
                options.add(radio3);
                PdfLayer.addOCGRadioGroup(fromDocument, options);
                options = new ArrayList<>();
                PdfLayer radio4 = new PdfLayer("Radio4", fromDocument);
                options.add(radio4);
                pdfResource.addProperties(radio4.getPdfObject());
                PdfLayer.addOCGRadioGroup(fromDocument, options);

                // ON
                PdfLayer on1 = new PdfLayer("On1", fromDocument);
                on1.setOn(true);
                pdfResource.addProperties(on1.getPdfObject());
                PdfLayer on2 = new PdfLayer("On2", fromDocument);
                on2.setOn(true);

                // OFF
                PdfLayer off1 = new PdfLayer("Off1", fromDocument);
                off1.setOn(false);
                pdfResource.addProperties(off1.getPdfObject());
                PdfLayer off2 = new PdfLayer("Off2", fromDocument);
                off2.setOn(false);

                pdfResource.makeIndirect(fromDocument);

                PdfOCProperties ocProperties = fromDocument.getCatalog().getOCProperties(true);
                PdfDictionary ocPropertiesPdfObject = ocProperties.getPdfObject();

                String ocPropertiesAsString =
                        String.format("<<"
                                + "/Creator (CreatorName)"
                                + "/Name (Name)"
                                + "/BaseState /OFF"
                                + "/AS [<</Event /View /Category [/Zoom] /OCGs [ %d %d R ] >>]"
                                + "/Intent /Design"
                                + "/ListMode /VisiblePages"
                                + ">>", locked1.getIndirectReference().getObjNumber(), locked1.getIndirectReference().getGenNumber());

                PdfObject deserialize = deserializer.deserialize(ocPropertiesAsString, context);
                ocPropertiesPdfObject.putAll((PdfDictionary) deserialize);

                PdfLayer noPrint1 = new PdfLayer("noPrint1", fromDocument);
                pdfResource.addProperties(noPrint1.getPdfObject());
                noPrint1.setPrint("Print", false);
            }
        }
    }

    private void getDocumentWithAllDFields() throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream("target/OCG.pdf")) {
            try (PdfDocument fromDocument = new PdfDocument(new PdfWriter(outputStream))) {
                PdfPage page = fromDocument.addNewPage();
                PdfResources pdfResource = page.getResources();

                // Order
                PdfLayer parent1 = new PdfLayer("Parent1", fromDocument);
                PdfLayer child1 = new PdfLayer("Child1", fromDocument);
                pdfResource.addProperties(child1.getPdfObject());
                parent1.addChild(child1);

                // Locked
                PdfLayer locked1 = new PdfLayer("Locked1", fromDocument);
                locked1.setLocked(true);
                pdfResource.addProperties(locked1.getPdfObject());
                PdfLayer locked2 = new PdfLayer("Locked2", fromDocument);
                locked2.setLocked(true);

                // RBGroups
                PdfLayer radio1 = new PdfLayer("Radio1", fromDocument);
                pdfResource.addProperties(radio1.getPdfObject());
                PdfLayer radio2 = new PdfLayer("Radio2", fromDocument);
                PdfLayer radio3 = new PdfLayer("Radio3", fromDocument);
                pdfResource.addProperties(radio3.getPdfObject());
                List<PdfLayer> options = new ArrayList<>();
                options.add(radio1);
                options.add(radio2);
                options.add(radio3);
                PdfLayer.addOCGRadioGroup(fromDocument, options);
                options = new ArrayList<>();
                PdfLayer radio4 = new PdfLayer("Radio4", fromDocument);
                options.add(radio4);
                pdfResource.addProperties(radio4.getPdfObject());
                PdfLayer.addOCGRadioGroup(fromDocument, options);

                // ON
                PdfLayer on1 = new PdfLayer("On1", fromDocument);
                on1.setOn(true);
                pdfResource.addProperties(on1.getPdfObject());
                PdfLayer on2 = new PdfLayer("On2", fromDocument);
                on2.setOn(true);

                // OFF
                PdfLayer off1 = new PdfLayer("Off1", fromDocument);
                off1.setOn(false);
                pdfResource.addProperties(off1.getPdfObject());
                PdfLayer off2 = new PdfLayer("Off2", fromDocument);
                off2.setOn(false);

                pdfResource.makeIndirect(fromDocument);
                PdfOCProperties ocProperties = fromDocument.getCatalog().getOCProperties(true);
                // Creator (will be not copied)
                ocProperties.getPdfObject()
                        .put(PdfName.Creator, new PdfString("CreatorName", PdfEncodings.UNICODE_BIG));
                // Name (will be automatically changed)
                ocProperties.getPdfObject().put(PdfName.Name, new PdfString("Name", PdfEncodings.UNICODE_BIG));
                // BaseState (will be not copied)
                ocProperties.getPdfObject().put(PdfName.BaseState, PdfName.OFF);
                // AS (will be automatically changed)
                PdfArray asArray = new PdfArray();
                PdfDictionary dict = new PdfDictionary();
                dict.put(PdfName.Event, PdfName.View);
                PdfArray categoryArray = new PdfArray();
                categoryArray.add(PdfName.Zoom);
                dict.put(PdfName.Category, categoryArray);
                PdfArray ocgs = new PdfArray();
                ocgs.add(locked1.getPdfObject());
                dict.put(PdfName.OCGs, ocgs);
                asArray.add(dict);
                ocProperties.getPdfObject().put(PdfName.AS, asArray);

                PdfLayer noPrint1 = new PdfLayer("noPrint1", fromDocument);
                pdfResource.addProperties(noPrint1.getPdfObject());
                noPrint1.setPrint("Print", false);
                // Intent (will be not copied)
                ocProperties.getPdfObject().put(PdfName.Intent, PdfName.Design);
                // ListMode (will be not copied)
                ocProperties.getPdfObject().put(PdfName.ListMode, PdfName.VisiblePages);
            }
        }
    }
}
