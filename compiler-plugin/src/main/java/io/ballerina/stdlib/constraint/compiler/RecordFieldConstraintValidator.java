/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.stdlib.constraint.compiler;

import io.ballerina.compiler.api.symbols.RecordFieldSymbol;
import io.ballerina.compiler.api.symbols.Symbol;
import io.ballerina.compiler.api.symbols.TypeSymbol;
import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.RecordFieldNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.projects.plugins.AnalysisTask;
import io.ballerina.projects.plugins.SyntaxNodeAnalysisContext;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.diagnostics.DiagnosticSeverity;

import java.util.List;
import java.util.Optional;

import static io.ballerina.stdlib.constraint.compiler.Constants.RECORD;
import static io.ballerina.stdlib.constraint.compiler.ConstraintCompilerPluginUtils.validateConstraints;

/**
 * Validates Ballerina Constraint annotations on record fields.
 */
public class RecordFieldConstraintValidator implements AnalysisTask<SyntaxNodeAnalysisContext> {

    @Override
    public void perform(SyntaxNodeAnalysisContext syntaxNodeAnalysisContext) {
        List<Diagnostic> diagnostics = syntaxNodeAnalysisContext.semanticModel().diagnostics();
        boolean erroneousCompilation = diagnostics.stream()
                .anyMatch(d -> d.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR));
        if (erroneousCompilation) {
            return;
        }
        RecordFieldNode recordFieldNode = ((RecordFieldNode) syntaxNodeAnalysisContext.node());
        validateRecordFieldConstraints(syntaxNodeAnalysisContext, recordFieldNode);
    }

    private void validateRecordFieldConstraints(SyntaxNodeAnalysisContext ctx, RecordFieldNode recordFieldNode) {
        Optional<MetadataNode> optionalMetadataNode = recordFieldNode.metadata();
        if (optionalMetadataNode.isEmpty()) {
            return;
        }
        NodeList<AnnotationNode> annotationNodes = optionalMetadataNode.get().annotations();
        Optional<Symbol> optionalTypeSymbol = ctx.semanticModel().symbol(recordFieldNode);
        if (optionalTypeSymbol.isEmpty() || !(optionalTypeSymbol.get() instanceof RecordFieldSymbol)) {
            return;
        }
        RecordFieldSymbol recordFieldSymbol = (RecordFieldSymbol) optionalTypeSymbol.get();
        TypeSymbol fieldTypeSymbol = recordFieldSymbol.typeDescriptor();
        Node typeNode = recordFieldNode.typeName();
        String fieldType = typeNode.kind().equals(SyntaxKind.RECORD_TYPE_DESC) ? RECORD : typeNode.toString().trim();
        validateConstraints(ctx, annotationNodes, fieldType, fieldTypeSymbol);
    }
}
