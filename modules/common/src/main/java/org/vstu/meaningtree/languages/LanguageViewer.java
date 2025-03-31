package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.utils.NodeVisitor;

import java.util.List;

abstract public class LanguageViewer implements NodeVisitor<String> {
    private List<ConfigParameter> _cfg;

    public String visit(MeaningTree mt) {
        return visit(mt.getRootNode());
    }

    void setConfig(List<ConfigParameter> params) {
        _cfg = params;
    }

    protected ConfigParameter getConfigParameter(String paramName) {
        if (_cfg == null) {
            return null;
        }

        for (ConfigParameter param : _cfg) {
            if (param.getName().equals(paramName)) {
                return param;
            }
        }
        return null;
    }
}
