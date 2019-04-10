/*!
 *
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright (c) 2002-2018 Hitachi Vantara. All rights reserved.
 *
 */

package org.pentaho.commons.util.repository.type;

public abstract class PropertiesBase {

  public static final String OBJECTID = "ObjectId"; //$NON-NLS-1$
  public static final String URI = "Uri"; //$NON-NLS-1$
  public static final String OBJECTTYPEID = "ObjectTypeId"; //$NON-NLS-1$
  public static final String CREATEDBY = "CreatedBy"; //$NON-NLS-1$
  public static final String CREATIONDATE = "CreationDate"; //$NON-NLS-1$
  public static final String LASTMODIFIEDBY = "LastModifiedBy"; //$NON-NLS-1$
  public static final String LASTMODIFICATIONDATE = "LastModificationDate"; //$NON-NLS-1$
  public static final String CHANGETOKEN = "ChangeToken"; //$NON-NLS-1$

  protected String name;

  public PropertiesBase( String name ) {
    this.name = name;
  }

}