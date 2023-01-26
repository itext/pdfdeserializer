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
import com.itextpdf.io.source.PdfTokenizer.TokenType;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfIndirectReferenceFactory;
import com.itextpdf.kernel.pdf.PdfNull;
import com.itextpdf.kernel.pdf.PdfObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Deserializer {

    public PdfObject deserialize(String pdfObjectString, DeserializationContext context) throws IOException {
        return this.deserialize(ByteUtils.getIsoBytes(pdfObjectString), context);
    }

    public PdfObject deserialize(byte[] pdfObjectBytes, DeserializationContext context) throws IOException {
        PdfIndirectReferenceFactory.getInstance().setDocument(context.getPdfDocument());

        RandomAccessFileOrArray randomAccess = new RandomAccessFileOrArray(
                new RandomAccessSourceFactory().createSource(pdfObjectBytes)
        );

        try (PdfTokenizer tokenizer = new PdfTokenizer(randomAccess)) {
            IPdfObjectDeserializer pdfObjectDeserializer =
                    PdfObjectDeserializationFactory.getPdfObjectDeserializer(tokenizer);

            if (pdfObjectDeserializer != null) {
                return pdfObjectDeserializer.deserialize(tokenizer, context, true);
            }

            return PdfNull.PDF_NULL;
        }
    }

    public List<PdfObject> deserializeObjects(String pdfObjectsString, DeserializationContext context)
            throws IOException {
        if (context.getPdfDocument() != null) {
            PdfIndirectReferenceFactory.getInstance().setDocument(context.getPdfDocument());
        }

        List<PdfObject> objects = new ArrayList<>();

        RandomAccessFileOrArray randomAccess = new RandomAccessFileOrArray(
                new RandomAccessSourceFactory().createSource(ByteUtils.getIsoBytes(pdfObjectsString))
        );

        try (PdfTokenizer tokenizer = new PdfTokenizer(randomAccess)) {
            while ( tokenizer.getTokenType() != TokenType.EndOfFile ) {
                IPdfObjectDeserializer pdfObjectDeserializer =
                        PdfObjectDeserializationFactory.getPdfObjectDeserializer(tokenizer);

                if (pdfObjectDeserializer != null) {
                    objects.add(pdfObjectDeserializer.deserialize(tokenizer, context, true));
                }
            }
        }

        return objects;
    }
}
