package com.surenpi.jenkins.loadbalance;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.ListBoxModel;
import jenkins.model.ParameterizedJobMixIn;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author suren
 */
public final class BalanceProjectProperty extends JobProperty<Job<?, ?>>
{
    private long memory;
    private long disk;
    private Unit memoryUnit;
    private Unit diskUnit;

    @DataBoundConstructor
    public BalanceProjectProperty() {}

    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor
    {
        public static final String AGENT_LOAD = "agent_load";

        public boolean isApplicable(Class<? extends Job> jobType) {
            return ParameterizedJobMixIn.ParameterizedJob.class.isAssignableFrom(jobType);
        }

        @Nonnull
        @Override
        public String getDisplayName()
        {
            return "Agent Load";
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            BalanceProjectProperty tpp = req.bindJSON(
                    BalanceProjectProperty.class,
                    formData.getJSONObject(AGENT_LOAD)
            );

            if (tpp == null) {
                LOGGER.fine("Couldn't bind JSON");
                return null;
            }

            return tpp;
        }

        public ListBoxModel doFillDiskUnitItems()
        {
            return sortedUnits(new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    return Unit.valueOf(o1).equals(Unit.G) ? 1 : 0;
                }
            });
        }

        public ListBoxModel doFillMemoryUnitItems()
        {
            return sortedUnits(new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    return Unit.valueOf(o1).equals(Unit.M) ? 1 : 0;
                }
            });
        }

        private ListBoxModel sortedUnits(Comparator<String> comparator)
        {
            ListBoxModel listBoxModel = new ListBoxModel();

            List<String> items = Unit.list();
            items.sort(comparator);

            for(String item : items)
            {
                listBoxModel.add(item);
            }

            return listBoxModel;
        }
    }

    public long getMemory()
    {
        return memory;
    }

    @DataBoundSetter
    public void setMemory(long memory)
    {
        this.memory = memory;
    }

    public long getDisk()
    {
        return disk;
    }

    @DataBoundSetter
    public void setDisk(long disk)
    {
        this.disk = disk;
    }

    public Unit getMemoryUnit()
    {
        return memoryUnit;
    }

    @DataBoundSetter
    public void setMemoryUnit(Unit memoryUnit)
    {
        this.memoryUnit = memoryUnit;
    }

    public Unit getDiskUnit()
    {
        return diskUnit;
    }

    @DataBoundSetter
    public void setDiskUnit(Unit diskUnit)
    {
        this.diskUnit = diskUnit;
    }

    private static final Logger LOGGER = Logger.getLogger(BalanceProjectProperty.class.getName());
}