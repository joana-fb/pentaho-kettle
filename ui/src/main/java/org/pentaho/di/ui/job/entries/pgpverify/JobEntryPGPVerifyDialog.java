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


package org.pentaho.di.ui.job.entries.pgpverify;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.pgpverify.JobEntryPGPVerify;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.events.dialog.FilterType;
import org.pentaho.di.ui.core.events.dialog.SelectionAdapterFileDialogTextVar;
import org.pentaho.di.ui.core.events.dialog.SelectionAdapterOptions;
import org.pentaho.di.ui.core.events.dialog.SelectionOperation;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * This defines a PGP verify job entry.
 *
 * @author Samatar
 * @since 25-02-2011
 *
 */

public class JobEntryPGPVerifyDialog extends JobEntryDialog implements JobEntryDialogInterface {
  private static Class<?> PKG = JobEntryPGPVerify.class; // for i18n purposes, needed by Translator2!!

  private static final String[] EXTENSIONS = new String[] { "*" };

  private static final String[] FILETYPES = new String[] { BaseMessages.getString(
    PKG, "JobPGPVerify.Filetype.All" ) };

  private Label wlName;

  private Text wName;

  private FormData fdlName, fdName;

  private Label wlGPGLocation;

  private Button wbGPGLocation;

  private TextVar wGPGLocation;

  private FormData fdlGPGLocation, fdbGPGLocation, fdGPGLocation;

  private Label wlFilename;

  private Button wbFilename;

  private TextVar wFilename;

  private FormData fdlFilename, fdbFilename, fdFilename;

  private Label wluseDetachedSignature;

  private Button wuseDetachedSignature;

  private FormData fdluseDetachedSignature, fduseDetachedSignature;

  private Label wlDetachedFilename;

  private Button wbDetachedFilename;

  private TextVar wDetachedFilename;

  private FormData fdlDetachedFilename, fdbDetachedFilename, fdDetachedFilename;

  private Button wOK, wCancel;

  private Listener lsOK, lsCancel;

  private JobEntryPGPVerify jobEntry;

  private Shell shell;

  private SelectionAdapter lsDef;

  private boolean changed;

  private Group wSettings;
  private FormData fdSettings;

  public JobEntryPGPVerifyDialog( Shell parent, JobEntryInterface jobEntryInt, Repository rep, JobMeta jobMeta ) {
    super( parent, jobEntryInt, rep, jobMeta );
    jobEntry = (JobEntryPGPVerify) jobEntryInt;
    if ( this.jobEntry.getName() == null ) {
      this.jobEntry.setName( BaseMessages.getString( PKG, "JobPGPVerify.Name.Default" ) );
    }
  }

  public JobEntryInterface open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, props.getJobsDialogStyle() );
    props.setLook( shell );
    JobDialog.setShellImage( shell, jobEntry );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        jobEntry.setChanged();
      }
    };
    changed = jobEntry.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "JobPGPVerify.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // GPGLocation line
    wlName = new Label( shell, SWT.RIGHT );
    wlName.setText( BaseMessages.getString( PKG, "JobPGPVerify.Name.Label" ) );
    props.setLook( wlName );
    fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, -margin );
    fdlName.top = new FormAttachment( 0, margin );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wName );
    wName.addModifyListener( lsMod );
    fdName = new FormData();
    fdName.left = new FormAttachment( middle, 0 );
    fdName.top = new FormAttachment( 0, margin );
    fdName.right = new FormAttachment( 100, 0 );
    wName.setLayoutData( fdName );

    // ////////////////////////
    // START OF SERVER SETTINGS GROUP///
    // /
    wSettings = new Group( shell, SWT.SHADOW_NONE );
    props.setLook( wSettings );
    wSettings.setText( BaseMessages.getString( PKG, "JobPGPVerify.Settings.Group.Label" ) );

    FormLayout SettingsgroupLayout = new FormLayout();
    SettingsgroupLayout.marginWidth = 10;
    SettingsgroupLayout.marginHeight = 10;

    wSettings.setLayout( SettingsgroupLayout );

    // GPGLocation line
    wlGPGLocation = new Label( wSettings, SWT.RIGHT );
    wlGPGLocation.setText( BaseMessages.getString( PKG, "JobPGPVerify.GPGLocation.Label" ) );
    props.setLook( wlGPGLocation );
    fdlGPGLocation = new FormData();
    fdlGPGLocation.left = new FormAttachment( 0, 0 );
    fdlGPGLocation.top = new FormAttachment( wName, margin );
    fdlGPGLocation.right = new FormAttachment( middle, -margin );
    wlGPGLocation.setLayoutData( fdlGPGLocation );

    wbGPGLocation = new Button( wSettings, SWT.PUSH | SWT.CENTER );
    props.setLook( wbGPGLocation );
    wbGPGLocation.setText( BaseMessages.getString( PKG, "System.Button.Browse" ) );
    fdbGPGLocation = new FormData();
    fdbGPGLocation.right = new FormAttachment( 100, 0 );
    fdbGPGLocation.top = new FormAttachment( wName, 0 );
    wbGPGLocation.setLayoutData( fdbGPGLocation );

    wGPGLocation = new TextVar( jobMeta, wSettings, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wGPGLocation );
    wGPGLocation.addModifyListener( lsMod );
    fdGPGLocation = new FormData();
    fdGPGLocation.left = new FormAttachment( middle, 0 );
    fdGPGLocation.top = new FormAttachment( wName, margin );
    fdGPGLocation.right = new FormAttachment( wbGPGLocation, -margin );
    wGPGLocation.setLayoutData( fdGPGLocation );

    // Filename line
    wlFilename = new Label( wSettings, SWT.RIGHT );
    wlFilename.setText( BaseMessages.getString( PKG, "JobPGPVerify.Filename.Label" ) );
    props.setLook( wlFilename );
    fdlFilename = new FormData();
    fdlFilename.left = new FormAttachment( 0, 0 );
    fdlFilename.top = new FormAttachment( wGPGLocation, margin );
    fdlFilename.right = new FormAttachment( middle, -margin );
    wlFilename.setLayoutData( fdlFilename );

    wbFilename = new Button( wSettings, SWT.PUSH | SWT.CENTER );
    props.setLook( wbFilename );
    wbFilename.setText( BaseMessages.getString( PKG, "System.Button.Browse" ) );
    fdbFilename = new FormData();
    fdbFilename.right = new FormAttachment( 100, 0 );
    fdbFilename.top = new FormAttachment( wGPGLocation, 0 );
    wbFilename.setLayoutData( fdbFilename );

    wFilename = new TextVar( jobMeta, wSettings, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wFilename );
    wFilename.addModifyListener( lsMod );
    fdFilename = new FormData();
    fdFilename.left = new FormAttachment( middle, 0 );
    fdFilename.top = new FormAttachment( wGPGLocation, margin );
    fdFilename.right = new FormAttachment( wbFilename, -margin );
    wFilename.setLayoutData( fdFilename );

    wluseDetachedSignature = new Label( wSettings, SWT.RIGHT );
    wluseDetachedSignature.setText( BaseMessages.getString( PKG, "JobPGPVerify.useDetachedSignature.Label" ) );
    props.setLook( wluseDetachedSignature );
    fdluseDetachedSignature = new FormData();
    fdluseDetachedSignature.left = new FormAttachment( 0, 0 );
    fdluseDetachedSignature.top = new FormAttachment( wFilename, margin );
    fdluseDetachedSignature.right = new FormAttachment( middle, -margin );
    wluseDetachedSignature.setLayoutData( fdluseDetachedSignature );
    wuseDetachedSignature = new Button( wSettings, SWT.CHECK );
    props.setLook( wuseDetachedSignature );
    wuseDetachedSignature.setToolTipText( BaseMessages
      .getString( PKG, "JobPGPVerify.useDetachedSignature.Tooltip" ) );
    fduseDetachedSignature = new FormData();
    fduseDetachedSignature.left = new FormAttachment( middle, 0 );
    fduseDetachedSignature.top = new FormAttachment( wFilename, margin );
    fduseDetachedSignature.right = new FormAttachment( 100, -margin );
    wuseDetachedSignature.setLayoutData( fduseDetachedSignature );
    wuseDetachedSignature.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {

        enableDetachedSignature();

      }
    } );

    // DetachedFilename line
    wlDetachedFilename = new Label( wSettings, SWT.RIGHT );
    wlDetachedFilename.setText( BaseMessages.getString( PKG, "JobPGPVerify.DetachedFilename.Label" ) );
    props.setLook( wlDetachedFilename );
    fdlDetachedFilename = new FormData();
    fdlDetachedFilename.left = new FormAttachment( 0, 0 );
    fdlDetachedFilename.top = new FormAttachment( wuseDetachedSignature, margin );
    fdlDetachedFilename.right = new FormAttachment( middle, -margin );
    wlDetachedFilename.setLayoutData( fdlDetachedFilename );

    wbDetachedFilename = new Button( wSettings, SWT.PUSH | SWT.CENTER );
    props.setLook( wbDetachedFilename );
    wbDetachedFilename.setText( BaseMessages.getString( PKG, "System.Button.Browse" ) );
    fdbDetachedFilename = new FormData();
    fdbDetachedFilename.right = new FormAttachment( 100, 0 );
    fdbDetachedFilename.top = new FormAttachment( wuseDetachedSignature, 0 );
    wbDetachedFilename.setLayoutData( fdbDetachedFilename );

    wDetachedFilename = new TextVar( jobMeta, wSettings, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wDetachedFilename );
    wDetachedFilename.addModifyListener( lsMod );
    fdDetachedFilename = new FormData();
    fdDetachedFilename.left = new FormAttachment( middle, 0 );
    fdDetachedFilename.top = new FormAttachment( wuseDetachedSignature, margin );
    fdDetachedFilename.right = new FormAttachment( wbDetachedFilename, -margin );
    wDetachedFilename.setLayoutData( fdDetachedFilename );

    // Whenever something changes, set the tooltip to the expanded version:
    wDetachedFilename.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        wDetachedFilename.setToolTipText( jobMeta.environmentSubstitute( wDetachedFilename.getText() ) );
      }
    } );

    wbDetachedFilename.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wDetachedFilename, jobMeta,
      new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.FILE,
        new FilterType[] { FilterType.ALL }, FilterType.ALL ) ) );

    // Whenever something changes, set the tooltip to the expanded version:
    wFilename.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        wFilename.setToolTipText( jobMeta.environmentSubstitute( wFilename.getText() ) );
      }
    } );

    wbFilename.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wFilename, jobMeta,
            new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.FILE,
                    new FilterType[] { FilterType.ALL }, FilterType.ALL  ) ) );
    // Whenever something changes, set the tooltip to the expanded version:
    wGPGLocation.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        wGPGLocation.setToolTipText( jobMeta.environmentSubstitute( wGPGLocation.getText() ) );
      }
    } );

    wbGPGLocation.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wGPGLocation, jobMeta,
            new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.FILE,
                    new FilterType[] { FilterType.ALL }, FilterType.ALL  ) ) );
    fdSettings = new FormData();
    fdSettings.left = new FormAttachment( 0, margin );
    fdSettings.top = new FormAttachment( wName, margin );
    fdSettings.right = new FormAttachment( 100, -margin );
    wSettings.setLayoutData( fdSettings );
    // ///////////////////////////////////////////////////////////
    // / END OF Advanced SETTINGS GROUP
    // ///////////////////////////////////////////////////////////

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    FormData fd = new FormData();
    fd.right = new FormAttachment( 50, -10 );
    fd.bottom = new FormAttachment( 100, 0 );
    fd.width = 100;
    wOK.setLayoutData( fd );

    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    fd = new FormData();
    fd.left = new FormAttachment( 50, 10 );
    fd.bottom = new FormAttachment( 100, 0 );
    fd.width = 100;
    wCancel.setLayoutData( fd );

    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wCancel }, margin, wSettings );
    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOK.addListener( SWT.Selection, lsOK );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wName.addSelectionListener( lsDef );
    wGPGLocation.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    getData();
    enableDetachedSignature();
    BaseStepDialog.setSize( shell );

    shell.open();
    props.setDialogSize( shell, "JobPGPVerifyDialogSize" );
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return jobEntry;
  }

  public void dispose() {
    WindowProperty winprop = new WindowProperty( shell );
    props.setScreen( winprop );
    shell.dispose();
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wName.setText( Const.nullToEmpty( jobEntry.getName() ) );
    if ( jobEntry.getGPGLocation() != null ) {
      wGPGLocation.setText( jobEntry.getGPGLocation() );
    }
    if ( jobEntry.getFilename() != null ) {
      wFilename.setText( jobEntry.getFilename() );
    }
    if ( jobEntry.getDetachedfilename() != null ) {
      wDetachedFilename.setText( jobEntry.getDetachedfilename() );
    }
    wuseDetachedSignature.setSelection( jobEntry.useDetachedfilename() );

    wName.selectAll();
    wName.setFocus();
  }

  private void cancel() {
    jobEntry.setChanged( changed );
    jobEntry = null;
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wName.getText() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setText( BaseMessages.getString( PKG, "System.StepJobEntryNameMissing.Title" ) );
      mb.setMessage( BaseMessages.getString( PKG, "System.JobEntryNameMissing.Msg" ) );
      mb.open();
      return;
    }
    jobEntry.setName( wName.getText() );
    jobEntry.setGPGLocation( wGPGLocation.getText() );
    jobEntry.setFilename( wFilename.getText() );
    jobEntry.setDetachedfilename( wDetachedFilename.getText() );
    jobEntry.setUseDetachedfilename( wuseDetachedSignature.getSelection() );
    dispose();
  }

  private void enableDetachedSignature() {
    wlDetachedFilename.setEnabled( wuseDetachedSignature.getSelection() );
    wDetachedFilename.setEnabled( wuseDetachedSignature.getSelection() );
    wbDetachedFilename.setEnabled( wuseDetachedSignature.getSelection() );
  }

  public boolean evaluates() {
    return true;
  }

  public boolean isUnconditional() {
    return false;
  }

}
