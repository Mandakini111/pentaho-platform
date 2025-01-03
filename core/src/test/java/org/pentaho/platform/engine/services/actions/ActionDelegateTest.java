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


package org.pentaho.platform.engine.services.actions;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.commons.connection.IPentahoStreamSource;
import org.pentaho.platform.api.action.IAction;
import org.pentaho.platform.api.action.IStreamingAction;
import org.pentaho.platform.api.engine.ActionSequenceException;
import org.pentaho.platform.api.engine.IOutputHandler;
import org.pentaho.platform.api.engine.IPentahoDefinableObjectFactory.Scope;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.IRuntimeContext;
import org.pentaho.platform.api.engine.ISolutionEngine;
import org.pentaho.platform.api.engine.PluginBeanException;
import org.pentaho.platform.api.repository.ContentException;
import org.pentaho.platform.api.repository.IContentItem;
import org.pentaho.platform.engine.core.output.SimpleOutputHandler;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.core.system.boot.PlatformInitializationException;
import org.pentaho.platform.engine.services.ServiceTestHelper;
import org.pentaho.platform.engine.services.outputhandler.BaseOutputHandler;
import org.pentaho.platform.engine.services.solution.ActionDelegate;
import org.pentaho.platform.engine.services.solution.SolutionEngine;
import org.pentaho.platform.util.web.SimpleUrlFactory;
import org.pentaho.test.platform.engine.core.MicroPlatform;
import org.pentaho.test.platform.engine.core.PluginManagerAdapter;

/**
 * This JUnit test verifies the proper functioning of IActions as surrogate components. Let's not fool ourselves, these
 * are not really unit tests, rather they are integration tests created at as low altitude as possible to verify the
 * correct functioning of the ActionDelegate and proper execution of IAction. It would be too complex and probably
 * uselessly fragile to write real unit tests for the ActionDelegate.
 * <p>
 * ActionDelegate is the class that bridges the heavy IComponent layer with the lightweight IAction. Essentially it
 * brokers and manages IActions, so it is the actual unit under test with respect to this set of JUnit tests. A test
 * IAction, TestAction, is used to verify that ActionDelegate is working properly.
 * <p>
 * NOTE: the only way to get around having to define a repository here (which is out of scope of this project) and still
 * be able to test action sequence resources is to use only embedded resources in your test xactions. It is also very
 * important that ActionDelegate use the getInputStream API in actionsequence-dom to fetch resources, rather than
 * getDataSource (which does not support embedded resources).
 * 
 * @see ActionDelegate
 * @see IAction
 * @see IStreamingAction
 */
@SuppressWarnings( "nls" )
public class ActionDelegateTest {

  public static final String SOLUTION_PATH = "src/test/resources/solution";

  private MicroPlatform booter;

  private ByteArrayOutputStream out;

  IOutputHandler outputHandler;

  private static Map<String, String> veggieDataExpected = new HashMap<String, String>();

  private static List<Map<String, String>> fruitDataExpected = new ArrayList<Map<String, String>>();
  {
    veggieDataExpected.clear();
    veggieDataExpected.put( "name", "carrot" );
    veggieDataExpected.put( "color", "orange" );
    veggieDataExpected.put( "shape", "cone" );
    veggieDataExpected.put( "texture", "bumpy" );

    Map<String, String> orange = new HashMap<String, String>();
    orange.put( "name", "orange" );
    orange.put( "color", "orange" );
    orange.put( "shape", "sphere" );
    orange.put( "texture", "dimply" );
    Map<String, String> grapefruit = new HashMap<String, String>();
    grapefruit.put( "name", "grapefruit" );
    grapefruit.put( "color", "Yellow" );
    grapefruit.put( "shape", "sphere" );
    grapefruit.put( "texture", "dimply" );
    Map<String, String> cucumber = new HashMap<String, String>();
    cucumber.put( "name", "cucumber" );
    cucumber.put( "color", "green" );
    cucumber.put( "shape", "ellipsoid" );
    cucumber.put( "texture", "smooth" );
    fruitDataExpected.clear();
    fruitDataExpected.add( orange );
    fruitDataExpected.add( grapefruit );
    fruitDataExpected.add( cucumber );
  }

  @Before
  public void setUp() throws PlatformInitializationException {
    booter = new MicroPlatform( SOLUTION_PATH );
    booter.define( ISolutionEngine.class, SolutionEngine.class, Scope.GLOBAL );
    booter.define( IPluginManager.class, TestPluginManager.class, Scope.GLOBAL );
    booter.define( "contentrepo", TestOutputHandler.class, Scope.GLOBAL );
    booter.start();
  }

  @After
  public void tearDown() {
    booter.stop();
  }

  @Test
  public void testIndexedInputs() throws ActionSequenceException {
    TestIndexedInputsAction action1 = new TestIndexedInputsAction();

    execute( "testIndexedInputs.xaction", action1 );

    assertTrue( "messages list should have elements", action1.getMessages().size() > 0 );
    for ( int i = 0; i < 3; i++ ) {
      assertEquals( "action string type input \"messages_" + i + "\" is incorrect/not set", "message" + i, action1
          .getMessages().get( i ) );
    }
    assertEquals( "action string type input \"scalarMessage\" is incorrect/not set", "scalar message text", action1
        .getTextOfScalarMessage() );
  }

  @Test
  public void testMappedInput() throws ActionSequenceException {
    TestAction action1 = new TestAction();
    action1.setMessageBoard( "Action 1 was here!" );
    TestAction action2 = new TestAction();

    execute( "testMappedInput.xaction", action1, action2 );

    assertEquals( "action1 string type input \"message\" is incorrect/not set", "message text", action1.getMessage() );
    assertEquals( "action2 string type input \"message\" is incorrect/not set", "internalMessage text", action2
        .getMessage() );
    assertEquals( "should see the message from action1 here", "Action 1 was here!", action2.getMessageBoard() );
  }

  @Test
  public void testVarArgs() throws ActionSequenceException {
    TestVarArgsAction action = new TestVarArgsAction();

    execute( "testVarArgs.xaction", action );

    // first check that normal bean inputs are working
    assertEquals( "action1 string type input \"message\" is incorrect/not set", "message text", action.getMessage() );

    // then see if all the rest are passed via VarArgs
    assertNotNull( "varArgs not set", action.getVarArgs() );
    assertTrue( "varArg1 was not set", action.getVarArgs().containsKey( "varArg1" ) );
    assertEquals( "varArg1 has incorrect value", "varArg1 text", action.getVarArgs().get( "varArg1" ) );
    assertFalse( "varArg2 was set. We expect null values to be skipped.  See BeanUtil.setValue", action.getVarArgs()
        .containsKey( "varArg2" ) );
  }

  // TO-DO reduce a log for this method
  @Test
  public void testComponentDefinitionInputs() {
    TestAction action = new TestAction();

    try {
      execute( "testComponentDefinitionInputs.xaction", action );
    } catch ( ActionSequenceException e ) {
      // ignore errors here. we expect an error but want to check it with an assert
    }

    assertEquals( "string type input \"embeddedMessage\" is incorrect/not set", "embedded message text", action
        .getEmbeddedMessage() );
    assertEquals( "numeric type input \"embeddedNumber\" is incorrect/not set", new Integer( 2001 ), action
        .getEmbeddedNumber() );
    assertNull( "bad numeric \"badEmbeddedNumber\" should not have been set, is [" + action.getBadEmbeddedNumber()
        + "]", action.getBadEmbeddedNumber() );

    /*
     * Elements with only a text node and no sub-elements are treated by ASD as normal inputs. However, if an element in
     * the component-definition has sub-elements, ASD will not return it as an input, so this test will verify that
     * current behavior -- that a bean property for a complex element will not be set.
     */
    assertNull( "complex input (input with sub-elements) \"complexInputWithSubEelements\""
        + " is not currently supported as an input to an Action", action.getComplexInputWithSubEelements() );
  }

  @Test
  public void testCustomTypeIO() throws ActionSequenceException {
    TestAction.CustomType testCustomType = new TestAction.CustomType();
    TestAction action1 = new TestAction();
    action1.setCustom( testCustomType );
    TestAction action2 = new TestAction();

    execute( "testCustomTypeIO.xaction", action1, action2 );

    assertSame( "custom type object should have been passed from action1 to action2", testCustomType, action2
        .getCustom() );
  }

  /**
   * Here we are testing that actions can handle the old convention of using dashes instead of camelCase, for action
   * sequence inputs and outputs.
   * 
   * @throws ActionSequenceException
   */
  @Test
  public void testCompatibilityMode() throws ActionSequenceException {
    TestAllIOAction action = new TestAllIOAction();

    execute( "testCompatibilityMode.xaction", action );

    assertEquals( "string type input \"message\" is incorrect/not set", "Test 1..2..3", action.getMessage() );
    assertNotNull( "property-map type input \"veggie-data\" is not set", action.getVeggieData() );
    assertNotNull( "resource \"embedded-xml-resource\" is incorrect/not set", action.getEmbeddedXmlResource() );
    assertEquals( "output \"echo-message\" is not set or incorrect", "Test String Output", action.getEchoMessage() );
  }

  @Test
  public void testLogging() throws ActionSequenceException {
    TestLoggingSessionAwareAction action = new TestLoggingSessionAwareAction();

    execute( "testLoggingSessionAware.xaction", action );

    assertNotNull( "logger was not set on action", action.getLogger() );
  }

  @Test
  public void testSessionAwareness() throws ActionSequenceException {
    TestLoggingSessionAwareAction action = new TestLoggingSessionAwareAction();

    execute( "testLoggingSessionAware.xaction", action );

    assertNotNull( "session was not set on action", action.getSession() );
  }

  @Test
  public void testDefinitionAwareness() throws ActionSequenceException {
    TestDefinitionPreProcessingAction action = new TestDefinitionPreProcessingAction();

    execute( "testActionIOAllTypes.xaction", action );

    assertTrue( "message should be in input list", action.getInputNames().contains( "message" ) );
    assertTrue( "addresses should be in input list", action.getInputNames().contains( "addressees" ) );
    assertTrue( "echoMessage should be in output list", action.getOutputNames().contains( "echoMessage" ) );
    assertTrue( "myContentOutput should be in output list", action.getOutputNames().contains( "myContentOutput" ) );
  }

  @Test
  public void testPreProcessing() throws ActionSequenceException {
    TestDefinitionPreProcessingAction action = new TestDefinitionPreProcessingAction();

    assertFalse( "pre-execution method was called too early", action.isDoPreExecutionWasCalled() );
    assertFalse( "execute method was called too early", action.isExecuteWasCalled() );

    execute( "testActionIOAllTypes.xaction", action );

    assertTrue( "pre-execution method was not called", action.isDoPreExecutionWasCalled() );
    assertTrue( "execute method was not called", action.isExecuteWasCalled() );
  }

  @Test
  public void testStreaming() throws PlatformInitializationException, FileNotFoundException, ActionSequenceException {
    TestStreamingAction action1 = new TestStreamingAction();

    assertNull( action1.getMyContentOutputStream() );

    execute( "testStreaming.xaction", action1 );

    assertNotNull( "output stream was not set on action1", action1.getMyContentOutputStream() );
    assertTrue(
        "the fact that we are executing an IStreamingAction should have caused the responseExpected flag to be set",
        outputHandler.isResponseExpected() );
    assertEquals( "string type input \"message\" is incorrect/not set", "message input text", action1.getMessage() );
    assertTrue( "output stream should contain this text", out.toString().contains( "message input text" ) );
  }

  /**
   * Tests destination-less content outputs to make sure an Outputstream is still created and provided to the action
   * bean.
   * <p>
   * This test implies the following code snippets return non-null results for datasource and contentItem. What this
   * means to an action bean is it will be handed an outputstream for any output of type content that it declares as an
   * output, regardless of the fact that it may have a public counterpart with a destination. <code>
   * IPentahoStreamSource datasource = runtimeContext.getDataSource(actionInput.getName());
   * </code> or <code>
   * IActionParameter actionParameter = paramManager.getCurrentInput(parameterName);
   * IContentItem contentItem = actionParameter.getValue();
   * </code>
   * 
   * @throws ActionSequenceException
   */
  @Test
  public void testStreamingWithDestinationlessOutput() throws PlatformInitializationException, FileNotFoundException,
    ActionSequenceException {
    TestStreamingAction action1 = new TestStreamingAction();

    assertNull( action1.getMyContentOutputStream() );

    execute( "testStreamingWithDestinationlessOutput.xaction", action1 );

    assertNotNull( "output stream was not set on action1", action1.getMyContentOutputStream() );

    assertTrue( "output stream should contain this text", action1.getMyContentOutputStream().toString().contains(
        "message input text" ) );
  }

  /*
   * Here we specify an content type output with coming from the "request" destination, which is unsupported by current
   * IOutputHandler implementations.
   */
  @Test( expected = ActionSequenceException.class )
  public void testUnsupportedContentOutput() throws PlatformInitializationException, FileNotFoundException,
    ActionSequenceException {
    TestStreamingAction action1 = new TestStreamingAction();

    assertNull( action1.getMyContentOutputStream() );
    execute( "testUnsupportedContentOutput.xaction", action1 );
    // by this point, we should see a nice stack trace in the log indicating the (expected) error
  }

  @Test( expected = ActionSequenceException.class )
  public void testInputErrorCallbackFailedToSet() throws PlatformInitializationException, FileNotFoundException,
    ActionSequenceException {
    InputErrorCallbackFailedToSet action = new InputErrorCallbackFailedToSet();
    execute( "testInputErrorCallbackFailedToSet.xaction", action );
  }

  @Test
  public void testInputErrorCallbackNotWritable() throws PlatformInitializationException, FileNotFoundException,
    ActionSequenceException {
    InputErrorCallbackNotWritable action = new InputErrorCallbackNotWritable();
    execute( "testInputErrorCallbackNotWritable.xaction", action );
    assertNull( action.getInputNames() );
    assertNull( action.getOutputNames() );
  }

  @Test
  public void testResourceCallbackNotWritable() throws PlatformInitializationException, FileNotFoundException,
    ActionSequenceException {
    ResourceCallbackNotWritable action = new ResourceCallbackNotWritable();
    execute( "testResourceCallbackNotWritable.xaction", action );
    assertNull( action.getInputNames() );
    assertNull( action.getInputNames() );
  }

  @Test
  public void testActionIOAllTypes() throws PlatformInitializationException, FileNotFoundException,
    ActionSequenceException {
    TestAllIOAction action = new TestAllIOAction();
    execute( "testActionIOAllTypes.xaction", action );

    //
    // Check inputs
    //
    assertEquals( "string type input \"message\" is incorrect/not set", "Test 1..2..3", action.getMessage() );
    assertArrayEquals( "addresseess input is incorrect/not set", new String[] { "admin", "suzy", "fred", "sam" }, action
        .getAddressees().toArray() );
    assertEquals( "long type input \"count\" is incorrect/not set", new Long( 99 ), action.getCount() );
    assertNotNull( "property-map type input \"veggieData\" is not set", action.getVeggieData() );
    assertMapsEquivalent( "property-map type input \"veggieData\" is incorrect", veggieDataExpected, action
        .getVeggieData() );

    assertNotNull( "property-map-list type input \"fruitData\" is not set", action.getFruitData() );
    assertEquals( "property-map-list type input \"fruitData\" wrong size", fruitDataExpected.size(), action
        .getFruitData().size() );
    for ( int i = 0; i < fruitDataExpected.size(); i++ ) {
      assertMapsEquivalent( "property-map-list type input \"fruitData\" list element [" + i + "] is incorrect",
          fruitDataExpected.get( i ), action.getFruitData().get( i ) );
    }

    // Check resources
    assertNotNull( "resource \"embeddedXmlResource\" is incorrect/not set", action.getEmbeddedXmlResource() );

    // Check outputs
    assertEquals( "output \"echoMessage\" is not set or incorrect", "Test String Output", action.getEchoMessage() );

    // Check that that the various methods were invoked
    assertTrue( "execute method was not invoked", action.isExecuteWasCalled() );
  }

  private static boolean assertMapsEquivalent( String comment, Map<String, String> expected,
      Map<String, String> actual ) {
    for ( Map.Entry<String, String> entry : expected.entrySet() ) {
      String expectedKey = entry.getKey();
      String expectedVal = entry.getValue();
      assertNotNull( comment + ": entry for key [" + expectedKey + "] was expect and not found", actual.get(
          expectedKey ) );
      assertEquals( comment, expectedVal, actual.get( expectedKey ) );
    }
    return true;
  }

  public static class TestPluginManager extends PluginManagerAdapter {
    private ArrayList<IAction> actions = new ArrayList<IAction>();

    private int actionIdx = 0;

    public TestPluginManager() {
    }

    @Override
    public boolean isBeanRegistered( String beanId ) {
      return true;
    }

    public void addAction( IAction action ) {
      this.actions.add( action );
    }

    @Override
    public Object getBean( String beanId ) throws PluginBeanException {
      return actions.get( actionIdx++ );
    }
  }

  private void execute( String actionSequenceFile, IAction... actions ) throws ActionSequenceException {
    execute( actionSequenceFile, true, actions );
  }

  @SuppressWarnings( "unchecked" )
  private void execute( String actionSequenceFile, boolean exceptionOnError, IAction... actions )
    throws ActionSequenceException {
    TestPluginManager pm = (TestPluginManager) PentahoSystem.get( IPluginManager.class );
    for ( IAction action : actions ) {
      pm.addAction( action );
    }

    // content outputs will write to this stream
    out = new ByteArrayOutputStream();

    // create SimpleOutputHandler (to handle outputs of type "response.content")
    outputHandler = new SimpleOutputHandler( out, false );
    outputHandler.setOutputPreference( IOutputHandler.OUTPUT_TYPE_DEFAULT );

    IPentahoSession session = new StandaloneSession( "system" );
    ISolutionEngine solutionEngine = ServiceTestHelper.getSolutionEngine();
    outputHandler.setSession( session );

    String xactionStr =
        ServiceTestHelper.getXAction( "src/test/resources/solution/test/ActionDelegateTest", actionSequenceFile );

    // execute the action sequence, providing the outputHandler created above
    IRuntimeContext rc =
        solutionEngine.execute( xactionStr, actionSequenceFile, "action sequence to test the TestAction", false, true,
            null, false, new HashMap(), outputHandler, null, new SimpleUrlFactory( "" ), new ArrayList() );
    int status = rc.getStatus();
    if ( status == IRuntimeContext.PARAMETERS_FAIL || status == IRuntimeContext.RUNTIME_CONTEXT_RESOLVE_FAIL
        || status == IRuntimeContext.RUNTIME_STATUS_FAILURE || status == IRuntimeContext.RUNTIME_STATUS_INITIALIZE_FAIL
        || status == IRuntimeContext.RUNTIME_STATUS_SETUP_FAIL ) {
      throw new ActionSequenceException( "Action sequence failed!" );
    }
  }

  public static class TestOutputHandler extends BaseOutputHandler {
    public static ByteArrayOutputStream BOS = new ByteArrayOutputStream();

    private IContentItem contentItem = new IContentItem() {

      public void setName( String name ) {
      }

      public void setMimeType( String mimeType ) {
      }

      @SuppressWarnings( "unused" )
      public void removeVersion( String fileId ) {
      }

      @SuppressWarnings( "unused" )
      public void removeAllVersions() {
      }

      @SuppressWarnings( "unused" )
      public void makeTransient() {
      }

      @SuppressWarnings( "unused" )
      public String getUrl() {
        return null;
      }

      @SuppressWarnings( "unused" )
      public String getTitle() {
        return null;
      }

      @SuppressWarnings( "unused" )
      public Reader getReader() throws ContentException {
        return null;
      }

      public String getPath() {
        // TODO Auto-generated method stub
        return null;
      }

      public OutputStream getOutputStream( String actionName ) throws IOException {
        return BOS;
      }

      @SuppressWarnings( "unused" )
      public String getName() {
        // TODO Auto-generated method stub
        return null;
      }

      public String getMimeType() {
        // TODO Auto-generated method stub
        return null;
      }

      public InputStream getInputStream() throws ContentException {
        // TODO Auto-generated method stub
        return null;
      }

      @SuppressWarnings( "unused" )
      public String getId() {
        // TODO Auto-generated method stub
        return null;
      }

      @SuppressWarnings( { "unchecked", "unused" } )
      public List getFileVersions() {
        // TODO Auto-generated method stub
        return null;
      }

      @SuppressWarnings( "unused" )
      public long getFileSize() {
        // TODO Auto-generated method stub
        return 0;
      }

      @SuppressWarnings( "unused" )
      public String getFileId() {
        // TODO Auto-generated method stub
        return null;
      }

      @SuppressWarnings( "unused" )
      public Date getFileDateTime() {
        // TODO Auto-generated method stub
        return null;
      }

      public IPentahoStreamSource getDataSource() {
        // TODO Auto-generated method stub
        return null;
      }

      @SuppressWarnings( "unused" )
      public String getActionName() {
        // TODO Auto-generated method stub
        return null;
      }

      public void closeOutputStream() {
        // TODO Auto-generated method stub

      }
    };

    @Override
    public IContentItem getFileOutputContentItem() {
      return contentItem;
    }
  }
}
