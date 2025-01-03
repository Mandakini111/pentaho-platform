/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.test.platform.engine.security;

import org.pentaho.platform.api.engine.IAclHolder;
import org.pentaho.platform.api.engine.IAuthorizationPolicy;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.ISecurityHelper;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Stack;
import java.util.concurrent.Callable;

/**
 * Mock class to assist with unit testing. Please add to this class as needed. User: dkincade
 */
public class MockSecurityHelper implements ISecurityHelper {
  /**
   * The current user
   */
  private String currentUser;

  /**
   * The stack of users
   */
  private Stack<String> userStack = new Stack<String>();

  public void becomeUser( final String principalName ) {
    userStack.push( currentUser );
    currentUser = principalName;
  }

  public void becomeUser( final String principalName, final IParameterProvider paramProvider ) {
    becomeUser( principalName );
  }

  public <T> T runAsUser( final String principalName, final Callable<T> callable ) throws Exception {
    becomeUser( principalName );
    return callable.call();
  }

  public <T> T
    runAsUser( final String principalName, final IParameterProvider paramProvider, final Callable<T> callable )
    throws Exception {
    becomeUser( principalName );
    return callable.call();
  }

  public <T> T runAsAnonymous( final Callable<T> callable ) throws Exception {
    becomeUser( "unauthenticated" );
    return callable.call();
  }

  public boolean isPentahoAdministrator( final IPentahoSession session ) {
    IAuthorizationPolicy policy = PentahoSystem.get( IAuthorizationPolicy.class );
    return policy.isAllowed( "org.pentaho.repository.read" )
      && policy.isAllowed( "org.pentaho.repository.create" )
      && ( policy.isAllowed( "org.pentaho.security.administerSecurity" ) );
  }

  public boolean isGranted( final IPentahoSession session, final GrantedAuthority role ) {
    return false;
  }

  public boolean canHaveACLS( final ISolutionFile aFile ) {
    return false;
  }

  @Deprecated
  public boolean hasAccess( final IAclHolder aHolder, final int actionOperation, final IPentahoSession session ) {
    return false;
  }

  public Authentication createAuthentication( final String principalName ) {
    return null;
  }

  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public Authentication getAuthentication( final IPentahoSession ignoredSession, final boolean ignoredAllowAnonymous ) {
    return getAuthentication();
  }

  public <T> T runAsSystem( final Callable<T> callable ) throws Exception {
    becomeUser( "system" );
    return callable.call();
  }

  /**
   * Returns the current user (for recording who the current user is)
   */
  public String getCurrentUser() {
    return currentUser;
  }
}
