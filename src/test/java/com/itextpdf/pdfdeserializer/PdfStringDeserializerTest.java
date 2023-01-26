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
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PdfStringDeserializerTest {

    @Test
    public void normalTest() throws IOException {
        String s = "(Hello World)";
        Deserializer deserializer = new Deserializer();
        PdfObject deserialize = deserializer.deserialize(ByteUtils.getIsoBytes(s), new DeserializationContext());

        Assertions.assertTrue(deserialize.isString());
        PdfString pdfString = (PdfString) deserialize;
        Assertions.assertEquals(new PdfString("Hello World"), pdfString);
    }

    @Test
    public void normalParenthesesTest() throws IOException {
        String s = "((Hello World))";
        Deserializer deserializer = new Deserializer();
        PdfObject deserialize = deserializer.deserialize(ByteUtils.getIsoBytes(s), new DeserializationContext());

        Assertions.assertTrue(deserialize.isString());
        PdfString pdfString = (PdfString) deserialize;
        Assertions.assertEquals(new PdfString("(Hello World)"), pdfString);
        Assertions.assertFalse(pdfString.isHexWriting());
    }

    @Test
    public void hexTest() throws IOException {
        String s = "<AE151224>";
        Deserializer deserializer = new Deserializer();
        PdfObject deserialize = deserializer.deserialize(ByteUtils.getIsoBytes(s), new DeserializationContext());

        Assertions.assertTrue(deserialize.isString());
        PdfString pdfString = (PdfString) deserialize;
        Assertions.assertTrue(pdfString.isHexWriting());
    }
}
