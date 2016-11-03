package com.study.processor;

import com.study.processor.model.Rule;
import org.w3c.css.sac.*;
import org.w3c.css.sac.helpers.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DemoSAC implements DocumentHandler {
private boolean inMedia = false;
private boolean inStyleRule = false;
private int propertyCounter = 0;
private List<Rule> rules = new ArrayList<Rule>();

public void startMedia(SACMediaList media) throws CSSException {
  inMedia = true;
}

public void endMedia(SACMediaList media) throws CSSException {
  inMedia = false;
}

public void startSelector(SelectorList patterns) throws CSSException {
  if (!inMedia) {
    inStyleRule = true;
    propertyCounter = 0;
  }
}

public void endSelector(SelectorList patterns) throws CSSException {
  if (!inMedia) {
    System.out.println("Found " + propertyCounter + " properties.");
  }
  inStyleRule = false;

}

public void property(String name, LexicalUnit value, boolean important)
        throws CSSException {
  if (inStyleRule) {
    propertyCounter++;
  }
}

public static void main(String[] args) throws Exception {
  System.setProperty("org.w3c.css.sac.parser", "com.steadystate.css.parser.SACParserCSS3");

  InputSource source = new InputSource();
  URL uri = new URL("file", null, -1, "/home/aborovyk/projects/study/css-parser/src/main/resources/android.css");
  InputStream stream = uri.openStream();

  source.setByteStream(stream);
  source.setURI(uri.toString());
  ParserFactory parserFactory = new ParserFactory();
  Parser parser = parserFactory.makeParser();

  parser.setDocumentHandler(new DemoSAC());
  parser.parseStyleSheet(source);
  stream.close();
}

public void startFontFace() {
}

public void endFontFace() {
}

public void startPage(String name, String pseudoPage) {
}

public void endPage(String name, String pseudoPage) {
}

public void importStyle(String uri, SACMediaList media,
                        String defaultNamespaceURI) {
}

public void namespaceDeclaration(String prefix, String uri) {
}

public void ignorableAtRule(String atRule) {
}

public void comment(String text) {
}

public void startDocument(InputSource source) {
}

public void endDocument(InputSource source) {
}
}