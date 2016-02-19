
Android Material Design Icon Generator Plugin
=============================================

This plugin help you to set [material design icons](https://github.com/google/material-design-icons) to your Android project.

![capture](https://github.com/cat9/android-material-design-icon-generator-plugin/blob/master/docs/capture.gif)

#Installation

##Manually

1. Download the [MaterialDesignIconGeneratorPlugin.jar](https://github.com/cat9/android-material-design-icon-generator-plugin/blob/master/MaterialDesignIconGeneratorPlugin.jar)

On MAC:

2. `Preference > Plugins > Install plugin from disk...` Select MaterialDesignIconGeneratorPlugin.jar above.
 
On Linux or Windows:

2. `File > Settings... > IDE Settings > Plugins > Install plugin from disk...` Select MaterialDesignIconGeneratorPlugin.jar above.

3. Restart IntelliJ/Android Studio to activate plugin.

##Install IntelliJ Plugin Repositories

sorry,it do not work at this time.


# Development

```
$ git clone https://github.com/konifar/android-material-design-icon-generator-plugin.git
$ cd android-material-design-icon-generator-plugin
$ git submodule init
$ git submodule foreach git pull origin master
$ python script/copy_icons.py
$ python script/create_template.py
```



#License

```
Copyright 2014-2015 Yusuke Konishi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
