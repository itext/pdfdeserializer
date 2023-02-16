# pdfDeserializer

## Summary

pdfDeserializer is a tool that deserializes your PDF syntax into iText objects. It takes a String representation of any of the standard PDF objects:

| Token | Object |
|:-:|:-:|
| << ... >> | Dictionary |
| [ ... ] | Array |
| /Name | Name |
| ( abc ) | String (Literal) |
| < 616263 > | String (Hexadecimal) |
| 1.0 | Number |
| 1 0 R | Indirect Reference |

And it will turn that into a usable com.itextpdf.kernel.pdf.PdfObject instance (of the appropriate subclass of course).



For example:
```
<</Type/Example/Reason (Because)>>
```

Would become:
| Dictionary: | |
| :-- | :-- |
| Name{ Type } | Name{ Example } |
| Name{ Reason } | String{ Because }|

## Building

This project is built using maven. After cloning, run the following command:

```bash
mvn package
```

After a successful build, your target folder should contain a `pdfDeserializer-x.y.z.jar` which you can then use in your projects!

You can also use `mvn install` so that the artifact is automatically installed into your local maven repository!


## Usage

Add the pdfDeserializer dependency to your project and you can start using the tool as a high level tool:

```java
String s = "<</Hello /World>>";
Deserializer deserializer = new Deserializer();
PdfObject deserializedDictionary = deserializer.deserialize(ByteUtils.getIsoBytes(s), new DeserializationContext());
```


## Disclaimer

This is an experimental tool, not an iText product. It is provided to the
community under the terms of the AGPL (see [LICENSE](LICENSE.md)) on an as-is
basis.
