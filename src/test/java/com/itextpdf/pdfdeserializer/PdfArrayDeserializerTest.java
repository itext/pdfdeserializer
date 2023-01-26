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
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.pdfdeserializer.deserializers.PdfArrayDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PdfArrayDeserializerTest {

    @Test
    public  void arrayTest() throws IOException {
        PdfArray array = new PdfArray();
        array.add(new PdfName("Hello"));
        array.add(new PdfNumber(1234));

        String arrayAsString = array.toString();
        byte[] isoBytes = ByteUtils.getIsoBytes(arrayAsString);

        RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(isoBytes));

        try (PdfTokenizer tokenizer = new PdfTokenizer(raf)) {
            tokenizer.nextToken();
            PdfArray actual = new PdfArrayDeserializer().deserialize(tokenizer,
                    new DeserializationContext().setStrictnessLevel(
                            StrictnessLevel.CONSERVATIVE), true);
            Assertions.assertEquals(array, actual);
        }
    }

    @Test
    public void testWithInputString() throws IOException {
        String arrayString = "[/Hello 1 3]";
        Assertions.assertTrue(getPdfArray(arrayString).size() == 3);
    }

    @Test
    public void test2() throws IOException {
        String arrayString = "[true false null]";
        Assertions.assertTrue(getPdfArray(arrayString).size() == 3);
    }

    @Test
    public void nestedArray() throws IOException {
        String arrayString = "[/Type [1 2 3]]";
        PdfArray pdfArray = getPdfArray(arrayString);
        Assertions.assertEquals(2, pdfArray.size());
        PdfObject pdfObject = pdfArray.get(1);
        Assertions.assertTrue(pdfObject.isArray());
        PdfArray nestedArray = (PdfArray) pdfObject;
        Assertions.assertEquals(3, nestedArray.size());
    }

    @Test
    public void differentKidsTest() throws IOException {

        String arrayString = "[/Type 1 null true (Hello) [1 2 3] 1 0 R]";

        PdfArray pdfArray = getPdfArray(arrayString);
        Assertions.assertEquals(7, pdfArray.size());
        Assertions.assertTrue(pdfArray.get(0).isName());
        Assertions.assertTrue(pdfArray.get(1).isNumber());
        Assertions.assertTrue(pdfArray.get(2).isNull());
        Assertions.assertTrue(pdfArray.get(3).isBoolean());
        Assertions.assertTrue(pdfArray.get(4).isString());
        Assertions.assertTrue(pdfArray.get(5).isArray());
        Assertions.assertTrue(pdfArray.get(6, false).isIndirectReference());
    }

    private PdfArray getPdfArray(String arrayString) throws IOException {
        Deserializer deserializer = new Deserializer();
        PdfObject deserialize = deserializer.deserialize(ByteUtils.getIsoBytes(arrayString),
                new DeserializationContext());

        Assertions.assertTrue(deserialize.isArray());

        return (PdfArray) deserialize;
    }
}
