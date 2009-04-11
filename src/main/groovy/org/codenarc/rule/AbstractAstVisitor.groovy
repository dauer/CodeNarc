/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.control.SourceUnit
import org.codenarc.source.SourceCode

/**
 * Abstract superclass for Groovy AST Visitors used with Rules
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractAstVisitor extends ClassCodeVisitorSupport implements AstVisitor {
    public static final MAX_SOURCE_LINE_LENGTH = 60
    public static final SOURCE_LINE_LAST_SEGMENT_LENGTH = 12
    List violations = []
    Rule rule
    SourceCode sourceCode
    private Set visited = new HashSet()

    /**
     * Check if the AST expression has already been visited
     * @param expression - the AST expression to check
     * @return true if the AST expression has already been visited
     */
    protected isAlreadyVisited(expression) {
        return visited.contains(expression)
    }

    /**
     * Register the AST expression as having been visited
     * @param expression - the AST expression to register
     */
    protected registerAsVisited(expression) {
        visited << expression
    }

    /**
     * Return the source line corresponding to the specified Statement
     * @param statement - the Groovy AST Statement
     */
    protected String sourceLine(ASTNode statement) {
        // TODO Handle statements that cross multiple lines?
        return sourceCode.line(statement.lineNumber-1)
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     * @param node - the Groovy AST Node
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(ASTNode node, message=null) {
        def lineNumber = node.lineNumber
        if (lineNumber >= 0) {
            def sourceLine = sourceLine(node)
            addViolation(sourceLine, lineNumber, message)
        }
    }

    /**
     * Add a new Violation to the list of violations found by this visitor
     * @param sourceLine - the sourceLine for the violation
     * @param lineNumber - the line number of the violation
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(String sourceLine, int lineNumber, String message=null) {
        violations.add(new Violation(rule:rule, sourceLine:sourceLine, lineNumber:lineNumber, message:message))
    }

    protected SourceUnit getSourceUnit() {
        return source
    }

}