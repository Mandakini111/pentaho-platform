/*! ******************************************************************************
 *
 * Pentaho Community Edition
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.31 at 02:27:39 PM EDT 
//

package org.pentaho.platform.plugin.services.importer.mimeType.bindings;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * org.pentaho.platform.plugin.services.importer.mimeType.bindings package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

  private static final QName _ImportHandlerMimeTypeDefinitions_QNAME = new QName( "http://www.pentaho.com/schema/",
      "ImportHandlerMimeTypeDefinitions" );

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
   * org.pentaho.platform.plugin.services.importer.mimeType.bindings
   * 
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link ImportHandlerMimeTypeDefinitionsDto }
   * 
   */
  public ImportHandlerMimeTypeDefinitionsDto createImportHandlerMimeTypeDefinitionsDto() {
    return new ImportHandlerMimeTypeDefinitionsDto();
  }

  /**
   * Create an instance of {@link MimeTypeDefinitionsDto }
   * 
   */
  public MimeTypeDefinitionsDto createMimeTypeDefinitionsDto() {
    return new MimeTypeDefinitionsDto();
  }

  /**
   * Create an instance of {@link ImportHandlerDto }
   * 
   */
  public ImportHandlerDto createImportHandlerDto() {
    return new ImportHandlerDto();
  }

  /**
   * Create an instance of {@link MimeTypeDefinitionDto }
   * 
   */
  public MimeTypeDefinitionDto createMimeTypeDefinitionDto() {
    return new MimeTypeDefinitionDto();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link ImportHandlerMimeTypeDefinitionsDto }{@code >}
   * 
   */
  @XmlElementDecl( namespace = "http://www.pentaho.com/schema/", name = "ImportHandlerMimeTypeDefinitions" )
  public JAXBElement<ImportHandlerMimeTypeDefinitionsDto> createImportHandlerMimeTypeDefinitions(
      ImportHandlerMimeTypeDefinitionsDto value ) {
    return new JAXBElement<ImportHandlerMimeTypeDefinitionsDto>( _ImportHandlerMimeTypeDefinitions_QNAME,
        ImportHandlerMimeTypeDefinitionsDto.class, null, value );
  }

}
