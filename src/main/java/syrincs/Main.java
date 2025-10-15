// src/main/java/syrincs/Main.java
package syrincs;

import syrincs.b_application.UseCaseInteractor;
import syrincs.c_adapters.JdkMidiOutputAdapter;
import syrincs.c_adapters.cli.CliController;
import syrincs.c_adapters.postgres.PostgresHindemithChordRepository;

public class Main {
    public static void main(String[] args) throws Exception {
        // Bootstrap interactor with MIDI and (optional) DB repository
        var midiAdapter = new JdkMidiOutputAdapter();
        var dbCfg = syrincs.d_frameworksAndDrivers.AppConfig.loadDbConfig(args);
        var repo = new PostgresHindemithChordRepository(dbCfg.url, dbCfg.user, dbCfg.password);
        var interactor = new UseCaseInteractor(midiAdapter, repo);

        var controller = new CliController(interactor);
        controller.handle(args);
    }
}
