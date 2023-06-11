package com.apzda.build

import com.lesfurets.jenkins.unit.declarative.DeclarativePipelineTest
import org.junit.Before
import org.junit.Test

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.ProjectSource.projectSource

/**
 *
 * @author 宁广丰 <ninggf@yueworld.cn>
 * @version 1.0.0
 * @since 1.0.0
 */
class TestExampleDeclarativeJob extends DeclarativePipelineTest {
    @Before
    @Override
    void setUp() throws Exception {
        super.setUp()
        helper.addShMock("java -version", "1.8.0", 0)
        addEnvVar("BUILD_ID", "1")
        def library = library().name('commons')
                .defaultVersion('1.2.1')
                .allowOverride(true)
                .implicit(true)
                .targetPath('<notNeeded>')
                .retriever(projectSource())
                .build()
        helper.registerSharedLibrary(library)
    }

    @Test
    void should_execute_without_errors() throws Exception {
        runScript("src/test/resources/Jenkinsfile")

        assertJobStatusSuccess()
        printCallStack()
    }
}
