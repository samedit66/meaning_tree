package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.configs.Config;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.ParenthesesFiller;
import org.vstu.meaningtree.utils.tokens.OperatorToken;

import java.util.Optional;

abstract public class LanguageViewer {
    private Config _config;
    protected MeaningTree origin;

    public LanguageViewer() {
        this.parenFiller = new ParenthesesFiller(this::mapToToken);
    }

    public LanguageViewer(LanguageTranslator translator) {
        this.translator = translator;
        this.parenFiller = new ParenthesesFiller(this::mapToToken);
    }

    protected LanguageTranslator translator;
    protected ParenthesesFiller parenFiller;

    public abstract String toString(Node node);
    public abstract OperatorToken mapToToken(Expression expr);

    public String toString(MeaningTree mt) {
        origin = mt;
        return toString(mt.getRootNode());
    }

    void setConfig(Config config) {
        _config = config;
    }

    protected <P, T extends ConfigParameter<P>> Optional<P> getConfigParameter(Class<T> configClass) {
        return Optional.ofNullable(_config).flatMap(config -> config.get(configClass));
    }
}
