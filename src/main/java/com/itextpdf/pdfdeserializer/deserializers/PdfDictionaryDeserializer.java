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
package com.itextpdf.pdfdeserializer.deserializers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNull;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.pdfdeserializer.DeserializationContext;
import com.itextpdf.pdfdeserializer.IPdfObjectDeserializer;
import com.itextpdf.pdfdeserializer.PdfObjectDeserializationFactory;

import java.io.IOException;

public class PdfDictionaryDeserializer implements IPdfObjectDeserializer<PdfDictionary> {
    private PdfNameDeserializer pdfNameDeserializer;

    public PdfDictionaryDeserializer() {
        this.pdfNameDeserializer = new PdfNameDeserializer();
    }

    @Override
    public PdfDictionary deserialize(PdfTokenizer tokens, DeserializationContext context, boolean readAsDirectObject)
            throws IOException {
        PdfDictionary dic = new PdfDictionary();
        while (true) {
            if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic) {
                break;
            }
            tokens.nextValidToken();
            if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic) {
                break;
            }
            if (tokens.getTokenType() != PdfTokenizer.TokenType.Name) {
                tokens.throwError(
                        KernelExceptionMessageConstant.THIS_DICTIONARY_KEY_IS_NOT_A_NAME, tokens.getStringValue());
            }
            PdfName name = this.pdfNameDeserializer.deserialize(tokens, context, true);
            IPdfObjectDeserializer pdfObjectDeserializer = PdfObjectDeserializationFactory.getPdfObjectDeserializer(
                    tokens);
            PdfObject obj;
            if ( pdfObjectDeserializer == null ) {
                obj = PdfNull.PDF_NULL;
            } else {
                obj = pdfObjectDeserializer.deserialize(tokens, context, readAsDirectObject);
            }
            dic.put(name, obj);
        }
        return dic;
    }
}
