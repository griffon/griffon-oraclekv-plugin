/*
    griffon-oraclekv plugin
    Copyright (C) 2012 Andres Almiray

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * @author Andres Almiray
 */
class OraclekvGriffonPlugin {
    // the plugin version
    String version = '0.2'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '1.1.0 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'GNU Affero GPL 3.0'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = ''

    List authors = [
        [
            name: 'Andres Almiray',
            email: 'aalmiray@yahoo.com'
        ]
    ]
    String title = 'Oracle NoSQL support'
    String description = '''
The Oraclekv plugin enables lightweight access to [Oracle NoSQL][1] datastores.
This plugin does NOT provide domain classes nor dynamic finders like GORM does.

Usage
-----
Upon installation the plugin will generate the following artifacts in `$appdir/griffon-app/conf`:

 * OraclekvConfig.groovy - contains the store definitions.
 * BootstrapOraclekv.groovy - defines init/destroy hooks for data to be manipulated during app startup/shutdown.

A new dynamic method named `withOraclekv` will be injected into all controllers,
giving you access to an `oraclekv.store.KVStore` object, with which you'll be able
to make calls to the store. Remember to make all store calls off the EDT
otherwise your application may appear unresponsive when doing long computations
inside the EDT.

This method is aware of multiple stores. If no storeName is specified when calling
it then the default store will be selected. Here are two example usages, the first
queries against the default store while the second queries a store whose name has
been configured as 'internal'

    package sample
    class SampleController {
        def queryAllDataSources = {
            withOraclekv { storeName, store -> ... }
            withOraclekv('internal') { storeName, store -> ... }
        }
    }

This method is also accessible to any component through the singleton `griffon.plugins.oraclekv.OraclekvConnector`.
You can inject these methods to non-artifacts via metaclasses. Simply grab hold of a particular metaclass and call
`OraclekvEnhancer.enhance(metaClassInstance, oraclekvProviderInstance)`.

Configuration
-------------
### Dynamic method injection

The `withOraclekv()` dynamic method will be added to controllers by default. You can
change this setting by adding a configuration flag in `griffon-app/conf/Config.groovy`

    griffon.oraclekv.injectInto = ['controller', 'service']

### Events

The following events will be triggered by this addon

 * OraclekvConnectStart[config, storeName] - triggered before connecting to the store
 * OraclekvConnectEnd[storeName, store] - triggered after connecting to the store
 * OraclekvDisconnectStart[config, storeName, store] - triggered before disconnecting from the store
 * OraclekvDisconnectEnd[config, storeName] - triggered after disconnecting from the store

### Multiple Stores

The config file `OraclekvConfig.groovy` defines a default store block. As the name
implies this is the store used by default, however you can configure named stores
by adding a new config block. For example connecting to a store whose name is 'internal'
can be done in this way

    stores {
        internal {
            host = 'server.acme.com'
        }
    }

This block can be used inside the `environments()` block in the same way as the
default store block is used.

### Example

A trivial sample application can be found at [https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/oraclekv][2]

Testing
-------
The `withOraclekv()` dynamic method will not be automatically injected during unit testing, because addons are simply not initialized
for this kind of tests. However you can use `OraclekvEnhancer.enhance(metaClassInstance, oraclekvProviderInstance)` where 
`oraclekvProviderInstance` is of type `griffon.plugins.oraclekv.OraclekvProvider`. The contract for this interface looks like this

    public interface OraclekvProvider {
        Object withOraclekv(Closure closure);
        Object withOraclekv(String storeName, Closure closure);
        <T> T withOraclekv(CallableWithArgs<T> callable);
        <T> T withOraclekv(String storeName, CallableWithArgs<T> callable);
    }

It's up to you define how these methods need to be implemented for your tests. For example, here's an implementation that never
fails regardless of the arguments it receives

    class MyOraclekvProvider implements OraclekvProvider {
        Object withOraclekv(String storeName = 'default', Closure closure) { null }
        public <T> T withOraclekv(String storeName = 'default', CallableWithArgs<T> callable) { null }
    }

This implementation may be used in the following way

    class MyServiceTests extends GriffonUnitTestCase {
        void testSmokeAndMirrors() {
            MyService service = new MyService()
            OraclekvEnhancer.enhance(service.metaClass, new MyOraclekvProvider())
            // exercise service methods
        }
    }


[1]: http://www.oracle.com/technetwork/products/nosqldb/overview/index.html
[2]: https://github.com/aalmiray/griffon_sample_apps/tree/master/persistence/oraclekv
'''
}
