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

package org.pentaho.platform.repository2.unified.jcr.sejcr;

import javax.jcr.Credentials;

/**
 * Determines the credentials passed to session.login().
 * 
 * @author mlowery
 */
public interface CredentialsStrategy {
  Credentials getCredentials();
}
