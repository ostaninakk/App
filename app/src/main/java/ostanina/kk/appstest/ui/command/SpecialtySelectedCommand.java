package ostanina.kk.appstest.ui.command;

import ostanina.kk.appstest.ui.MainActivity;
import ostanina.kk.appstest.R;
import ostanina.kk.appstest.data.model.Specialty;
import ostanina.kk.appstest.ui.EmployeeListFragment;

public class SpecialtySelectedCommand extends Command {
    private MainActivity activity;
    private Specialty specialty;

    public SpecialtySelectedCommand(MainActivity activity, Specialty specialty) {
        this.activity = activity;
        this.specialty = specialty;
    }

    @Override
    public boolean execute() {
        EmployeeListFragment fragment = EmployeeListFragment.newInstance(specialty);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }
}
