# AsciiPanelView
[![Build Status](https://travis-ci.org/Prokky/AsciiPanelView.svg?branch=master)](https://travis-ci.org/Prokky/AsciiPanelView)
[![Download](https://api.bintray.com/packages/prokky/maven/asciipanelview/images/download.svg) ](https://bintray.com/prokky/maven/asciipanelview/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-AsciiPanelView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/6097)

**Port of AsciiPanel library to Android View on Kotlin**

![Screenshot](https://raw.githubusercontent.com/Prokky/AsciiPanelView/master/screenshots/cavy.png)

## Usage
To include this library to your project add dependency in **build.gradle** file:
```
dependencies {
    implementation 'com.prokkypew:asciipanelview:1.0'
}
```
## Documentation
Simply add AsciiPanelView to your layout.  
There are few XML attributes that may help you configuring the view:
```
<com.prokkypew.asciipanelview.AsciiPanelView
        android:id="@+id/panelView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:panelWidth="64"                     //Panel width in symbols. Default is 64
        app:panelHeight="27"                    //Panel height in symbols. Default is 27
        app:defaultCharColor="#ffffff"          //Character color. Default is Color.BLACK
        app:defaultBackgroundColor="#000000"    //Characted background color. Default is Color.WHITE
        app:fontFamily="font.ttf"/>             //Name of your custom font file in /assets folder
```

More documentation available at https://prokky.github.io/AsciiPanelView/

## Example
```kotlin
class MainActivity : AppCompatActivity(), AsciiPanelView.OnCharClickedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AsciiPanelView>(R.id.panel)
                .writeChar('g', 2, 2, Color.RED, Color.YELLOW)
                .setCursorPosition(14, 15)
                .writeCharWithColor('p', Color.RED, Color.YELLOW)
                .writeString("We", 16, 17, Color.RED, Color.YELLOW)
                .writeCenter("Center TEXT String", 5, Color.YELLOW, Color.CYAN)
                .onCharClickedListener = this
    }

    override fun onCharClicked(x: Int?, y: Int?, char: AsciiPanelView.ColoredChar) {
        Log.d("char", "char clicked:" + x + ":" + y + " = " + char.char)
        panel.clearRect(' ', 0, 1, panel.panelWidth, 1)
                .writeCenter("x:" + x + " y:" + y + " char:" + char.char, 1)
    }
}
```
More complicated examples of library usage you can find in another project of mine:[**Infinite Cavy Story**](https://github.com/Prokky/InfiniteCavyStory-Android)
## Thanks
Big thanks to **Trystan Spangler**, the author of original [**AsciiPanel**](https://github.com/trystan/AsciiPanel)

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
