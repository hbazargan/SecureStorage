This module is based on https://github.com/orhanobut/hawk with some changes. The default encryption was replaced by a new encryption method which is based on KeyStore.
Also, the new encryption method is based on https://github.com/temyco/security-workshop-sample.

The below diagram illustrates steps of data storing which uses in this module.

You can implement your custom module based on the contracts for each section of above diagram then inject it to module builder.

The below diagram illustrates flow of decision making in order to create keys.
