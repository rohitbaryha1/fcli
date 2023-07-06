package com.fortify.cli.functest.fcli.fod;

import static com.fortify.cli.functest.common.spec.FcliSessionType.FOD
import static com.fortify.cli.functest.common.spec.FcliSessionType.SSC

import com.fortify.cli.functest.common.spec.BaseFcliSpec
import com.fortify.cli.functest.common.spec.FcliSession

import spock.lang.Unroll

@FcliSession(FOD)
class FoDAppSpec extends BaseFcliSpec {
    @Unroll("fcli.app.list (#i)")
    def "fod.app.list"() {
        expect:
        fcli "fod", "app", "list"
        where:
        i << (1..5)
    }
}