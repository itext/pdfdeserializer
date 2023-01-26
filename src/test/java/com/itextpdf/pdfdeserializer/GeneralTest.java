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

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfIndirectReferenceFactory;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GeneralTest {

    @Test
    public void genericTest() throws IOException {
        String s = "<</Hello [1 0 R /World [1 2 3]]/Type>>";

        Deserializer deserializer = new Deserializer();
        PdfObject deserializedObject = deserializer.deserialize(ByteUtils.getIsoBytes(s), new DeserializationContext());

        System.out.println(deserializedObject.getType());
    }

    @Test
    public void genericTest2() throws IOException {
        String s = "<</Hello [1 0 R /World [1 2 3]]/Type true>>";

        PdfObject deserializedObject = new Deserializer()
                .deserialize(ByteUtils.getIsoBytes(s), new DeserializationContext());

        Assertions.assertTrue(deserializedObject.isDictionary());
    }

    @Test
    public void genericTest3() {
        PdfDictionary dictionary = new PdfDictionary();
        PdfName helloName = new PdfName("Hello");
        PdfArray outerArray = new PdfArray();
        PdfIndirectReference pdfIndirectReference = PdfIndirectReferenceFactory.getInstance().create(1, 0);
        outerArray.add(pdfIndirectReference);
        PdfName worldName = new PdfName("World");
        outerArray.add(worldName);
        PdfArray array = new PdfArray();
        array.add(new PdfNumber(1));
        array.add(new PdfNumber(2));
        array.add(new PdfNumber(3));
        outerArray.add(array);
        dictionary.put(helloName, outerArray);
        dictionary.put(PdfName.Type, PdfBoolean.TRUE);
    }

    @Test
    public void addToExistingDocumentAsOrphan() throws IOException {
        String inputFile = "src/test/resources/test.pdf";
        String outputFile = "src/test/resources/output.pdf";

        PdfReader reader = new PdfReader(Files.newInputStream(Paths.get(inputFile)));
        PdfWriter writer = new PdfWriter(
                Files.newOutputStream(Paths.get(outputFile))
        );

        PdfDocument pdfDocument = new PdfDocument(
                reader, writer
        );

        String s = "<</Hello [1 0 R /World [1 2 3]]/Type/Type>>";

        PdfObject deserializedObject = new Deserializer()
                .deserialize(
                        ByteUtils.getIsoBytes(s),
                        new DeserializationContext().setPdfDocument(pdfDocument)
                );

        pdfDocument.setFlushUnusedObjects(true);
        deserializedObject.makeIndirect(pdfDocument);

        pdfDocument.close();
    }

    @Test
    public void addToExistingDocument() throws IOException {
        String inputFile = "src/test/resources/test.pdf";
        String outputFile = "src/test/resources/output.pdf";

        PdfReader reader = new PdfReader(Files.newInputStream(Paths.get(inputFile)));
        PdfWriter writer = new PdfWriter(
                Files.newOutputStream(Paths.get(outputFile))
        );

        PdfDocument pdfDocument = new PdfDocument(
                reader, writer
        );

        String s = "<</Hello [1 0 R /World [1 2 3]]/Type/Type>>";
        PdfObject deserializedObject = new Deserializer()
                .deserialize(
                        ByteUtils.getIsoBytes(s),
                        new DeserializationContext().setPdfDocument(pdfDocument)
                );

        pdfDocument.getCatalog().put(new PdfName("Custom Entry"), deserializedObject);

        pdfDocument.close();
    }


    @Test
    public void multipleDictTest() throws IOException {
        String s = "<</Type/Hello>><</Type/World>>";

        Deserializer deserializer = new Deserializer();
        List<PdfObject> deserialize = deserializer.deserializeObjects(s, new DeserializationContext());
        System.out.println();
    }
}
