package org.eclipse.scada.samples.demo1.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

    public ApplicationWorkbenchWindowAdvisor ( final IWorkbenchWindowConfigurer configurer )
    {
        super ( configurer );
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor (
            final IActionBarConfigurer configurer )
    {
        return new ApplicationActionBarAdvisor ( configurer );
    }

    @Override
    public void preWindowOpen ()
    {
        final IWorkbenchWindowConfigurer configurer = getWindowConfigurer ();
        configurer.setInitialSize ( new Point ( 1200, 1024 ) );
        configurer.setShowCoolBar ( false );
        configurer.setShowStatusLine ( true );
        configurer.setShowProgressIndicator ( true );
        configurer.setShowMenuBar ( true );
        configurer.setTitle ( "Eclipse SCADA Demo Client" );
        configurer.setShellStyle ( SWT.SHELL_TRIM );
    }
}
