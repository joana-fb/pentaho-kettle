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


package org.pentaho.di.trans.steps.salesforceinsert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.bowl.DefaultBowl;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPluginType;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.BooleanLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;
import org.pentaho.di.trans.steps.salesforce.SalesforceMetaTest;
import org.pentaho.di.trans.steps.salesforce.SalesforceStepMeta;

public class SalesforceInsertMetaTest {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    PluginRegistry.addPluginType( ValueMetaPluginType.getInstance() );
    PluginRegistry.addPluginType( TwoWayPasswordEncoderPluginType.getInstance() );
    PluginRegistry.init( true );
    String passwordEncoderPluginID =
      Const.NVL( EnvUtil.getSystemProperty( Const.KETTLE_PASSWORD_ENCODER_PLUGIN ), "Kettle" );
    Encr.init( passwordEncoderPluginID );
  }

  @Test
  public void testErrorHandling() {
    SalesforceStepMeta meta = new SalesforceInsertMeta();
    assertTrue( meta.supportsErrorHandling() );
  }

  @Test
  public void testBatchSize() {
    SalesforceInsertMeta meta = new SalesforceInsertMeta();
    meta.setBatchSize( "20" );
    assertEquals( "20", meta.getBatchSize() );
    assertEquals( 20, meta.getBatchSizeInt() );

    // Pass invalid batch size, should get default value of 10
    meta.setBatchSize( "unknown" );
    assertEquals( "unknown", meta.getBatchSize() );
    assertEquals( 10, meta.getBatchSizeInt() );
  }

  @Test
  public void testCheck() {
    SalesforceInsertMeta meta = new SalesforceInsertMeta();
    meta.setDefault();
    List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
    meta.check( remarks, null, null, null, null, null, null, null, null, null );
    boolean hasError = false;
    for ( CheckResultInterface cr : remarks ) {
      if ( cr.getType() == CheckResult.TYPE_RESULT_ERROR ) {
        hasError = true;
      }
    }
    assertFalse( remarks.isEmpty() );
    assertTrue( hasError );

    remarks.clear();
    meta.setDefault();
    meta.setUsername( "user" );
    meta.setUpdateLookup( new String[]{ "SalesforceField" } );
    meta.setUpdateStream( new String[]{ "StreamField" } );
    meta.setUseExternalId( new Boolean[]{ false } );
    meta.check( remarks, null, null, null, null, null, null, null, null, null );
    hasError = false;
    for ( CheckResultInterface cr : remarks ) {
      if ( cr.getType() == CheckResult.TYPE_RESULT_ERROR ) {
        hasError = true;
      }
    }
    assertFalse( remarks.isEmpty() );
    assertFalse( hasError );
  }

  @Test
  public void testGetFields() throws KettleStepException {
    SalesforceInsertMeta meta = new SalesforceInsertMeta();
    meta.setDefault();
    RowMetaInterface r = new RowMeta();
    meta.getFields( DefaultBowl.getInstance(), r, "thisStep", null, null, new Variables(), null, null );
    assertEquals( 1, r.size() );
    assertEquals( "Id", r.getFieldNames()[0] );

    meta.setSalesforceIDFieldName( "id_field" );
    r.clear();
    meta.getFields( DefaultBowl.getInstance(), r, "thisStep", null, null, new Variables(), null, null );
    assertEquals( 1, r.size() );
    assertEquals( "id_field", r.getFieldNames()[0] );
  }

  @Test
  public void testSalesforceInsertMeta() throws KettleException {
    List<String> attributes = new ArrayList<String>();
    attributes.addAll( SalesforceMetaTest.getDefaultAttributes() );
    attributes.addAll( Arrays.asList( "batchSize", "salesforceIDFieldName", "updateLookup", "updateStream",
      "useExternalId", "rollbackAllChangesOnError" ) );
    Map<String, String> getterMap = new HashMap<String, String>();
    Map<String, String> setterMap = new HashMap<String, String>();
    Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidators = new HashMap<String, FieldLoadSaveValidator<?>>();
    fieldLoadSaveValidators.put( "updateLookup",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 50 ) );
    fieldLoadSaveValidators.put( "updateStream",
      new ArrayLoadSaveValidator<String>( new StringLoadSaveValidator(), 50 ) );
    fieldLoadSaveValidators.put( "useExternalId",
      new ArrayLoadSaveValidator<Boolean>( new BooleanLoadSaveValidator(), 50 ) );

    LoadSaveTester loadSaveTester =
      new LoadSaveTester( SalesforceInsertMeta.class, attributes, getterMap, setterMap,
        fieldLoadSaveValidators, new HashMap<String, FieldLoadSaveValidator<?>>() );

    loadSaveTester.testRepoRoundTrip();
    loadSaveTester.testXmlRoundTrip();
  }
}
