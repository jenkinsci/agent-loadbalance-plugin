/**
 * Project Name:job-restrictions-plugin
 * File Name:LoadBalanceRestriction.java
 * Package Name:com.synopsys.arc.jenkinsci.plugins.jobrestrictions.jobs
 * Date:Feb 9, 20186:33:08 PM
 * Authos:surenpi
 *
 */
package com.surenpi.jenkins.loadbalance;

import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.JobRestriction;
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.JobRestrictionDescriptor;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Job;
import hudson.model.Project;
import hudson.model.Queue.BuildableItem;
import hudson.model.Run;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * @author surenpi
 */
public class LoadBalanceRestriction extends JobRestriction {

    private boolean loadBalanceRestriction;

    @DataBoundConstructor
    public LoadBalanceRestriction(boolean loadBalanceRestriction)
    {
        this.loadBalanceRestriction = loadBalanceRestriction;
    }

    public boolean isLoadBalanceRestriction()
    {
        return loadBalanceRestriction;
    }

    /**
	 * @see com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.JobRestriction#canTake(BuildableItem)
	 */
	@Override
	public boolean canTake(BuildableItem item) {
        if(item.task instanceof Project)
        {
            Project project = (Project) item.task;

            return canTake(project);
        }

		return true;
	}

	/**
	 * @see com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.JobRestriction#canTake(Run)
	 */
	@Override
	public boolean canTake(Run run) {
	    return canTake(run.getParent());
	}

	private boolean canTake(Job job)
    {
        BalanceProjectProperty balance = (BalanceProjectProperty)
                job.getProperty(BalanceProjectProperty.class);

        if(loadBalanceRestriction)
        {
            long memory = balance.getMemory() * balance.getMemoryUnit().getOrigin();
            long disk = balance.getDisk() * balance.getDiskUnit().getOrigin();

            Runtime runtime = Runtime.getRuntime();
            if(runtime.freeMemory() < memory)
            {
                return false;
            }

            FilePath path = new FilePath(job.getBuildDir());
            try
            {
                long usableDiskSpace = path.getUsableDiskSpace();
                if(usableDiskSpace < disk)
                {
                    return false;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Extension
    public static class DescriptorImpl extends JobRestrictionDescriptor
    {
        @Override
        public String getDisplayName()
        {
            return "LoadBalanceRestriction";
        }
    }

}
