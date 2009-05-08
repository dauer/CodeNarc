/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for EmptySynchronizedStatementRule
 *
 * @author Chris Mair
 * @version $Revision: 24 $ - $Date: 2009-01-31 07:47:09 -0500 (Sat, 31 Jan 2009) $
 */
class EmptySynchronizedStatementRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptySynchronizedStatement'
    }

    void testApplyTo_EmptySynchronized() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    synchronized(lock) {
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'synchronized(lock) {')
    }

    void testApplyTo_Violation_SynchronizedContainsComment() {
        final SOURCE = '''
            synchronized(lock) {
                // TODO Should do something here
            }
        '''
        assertSingleViolation(SOURCE, 2, 'synchronized(lock) {')
    }

    void testApplyTo_NonEmptySynchronized() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    synchronized(lock) {
                        println "bad stuff happened"
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new EmptySynchronizedStatementRule()
    }

}