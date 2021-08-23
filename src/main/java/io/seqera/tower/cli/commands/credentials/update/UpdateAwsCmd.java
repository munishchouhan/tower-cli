package io.seqera.tower.cli.commands.credentials.update;

import io.seqera.tower.cli.commands.credentials.providers.AwsProvider;
import io.seqera.tower.cli.commands.credentials.providers.AbstractProvider;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(
        name = "aws",
        description = "Update AWS workspace credentials"
)
public class UpdateAwsCmd extends AbstractUpdateCmd {

    @Mixin
    protected AwsProvider provider;

    @Override
    protected AbstractProvider getProvider() {
        return provider;
    }
}
