/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.seqera.tower.cli;

import io.seqera.tower.cli.autocomplete.AutocompleteCmd;
import io.seqera.tower.cli.commands.RunCmd;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import io.seqera.tower.api.TowerApi;
import io.seqera.tower.ApiClient;

import java.util.concurrent.Callable;


@Command(
        name = "towr",
        description = "Nextflow Tower CLI",
        version = "Tower 0.1.0",
        mixinStandardHelpOptions = true
)
public class App implements Callable<Integer> {
    @Spec CommandSpec spec;

    private final AppConfig config;

    private final TowerApi api;

    public App() {
        this(new AppConfig());
    }

    public App(AppConfig config) {
        this.config = config;

        // Initialize API client
        ApiClient client = new ApiClient();
        client.setBasePath(config.getUrl());
        client.setBearerToken(config.getToken());
        api = new TowerApi(client);
    }

    public TowerApi getApi() {
        return api;
    }

    public AppConfig getConfig() {
        return config;
    }

    public static void main(String[] args) {
        App app = new App();
        CommandLine cmd = buildCmd(app);

        // Execute command
        int exitCode = cmd.execute(args);

        // End app
        System.exit(exitCode);
    }

    protected static CommandLine buildCmd(App app) {
        CommandLine cmd = new CommandLine(app)
                .addSubcommand(new RunCmd(app))
                .addSubcommand(new HelpCommand());

        // Add generate-completion command
        cmd = cmd.addSubcommand(new AutocompleteCmd(cmd));

        return cmd;
    }

    @Override
    public Integer call() {
        // if the command was invoked without subcommand, show the usage help
        spec.commandLine().usage(System.err);
        return -1;
    }

    public void println(String content) {
        spec.commandLine().getOut().println(content);
    }
}
