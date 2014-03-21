package org.eclipse.scada.samples.demo1.app;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{

    private static final String PERSPECTIVE_ID = "org.eclipse.scada.samples.demo1.app.perspective";

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor (
            final IWorkbenchWindowConfigurer configurer )
    {
        return new ApplicationWorkbenchWindowAdvisor ( configurer );
    }

    @Override
    public void initialize ( final IWorkbenchConfigurer configurer )
    {
        super.initialize ( configurer );
        configurer.setSaveAndRestore ( true );
    }

    @Override
    public String getInitialWindowPerspectiveId ()
    {
        return PERSPECTIVE_ID;
    }

}
