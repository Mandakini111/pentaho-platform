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

package org.pentaho.platform.plugin.action.chartbeans;

public class ChartSystemInitializationException extends Exception {

  private static final long serialVersionUID = 8208228972280627728L;

  public ChartSystemInitializationException( String s ) {
    super( s );
  }

  public ChartSystemInitializationException( Throwable t ) {
    super( t );
  }
}
