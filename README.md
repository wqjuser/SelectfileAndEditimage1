# SelectfileAndEditimage1
## 安卓文件选择和拍照完成返回图片编辑的库  
### 这个库是经过https://github.com/siwangqishiq/ImageEditor-Android 这个库的修改而来的，感谢作者，目前这个库只支持Android 5.0以上的设备，后续会增加低版本的适配  
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[ ![Download](https://api.bintray.com/packages/mrwen/ImageAndFile/selectfileandeditimage/images/download.svg) ](https://bintray.com/mrwen/ImageAndFile/selectfileandeditimage/_latestVersion)  
### 效果示例gif：  
![示例gif](https://github.com/wqjuser/SelectfileAndEditimage1/blob/master/example.gif)  
### 效果示例video  
[![示例video](http://img.youtube.com/vi/Ky-U-DwSLoM/0.jpg)](https://youtu.be/Ky-U-DwSLoM)   
# Usage  
#### 首先：  
##### compile 'com.wqj:selectfileandeditimage:1.0.1'  
#### 其次：  
##### 如果你的手机是Android 6.0的设备，请在application的文件中加入  
    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
      StrictMode.setVmPolicy(builder.build());
      builder.detectFileUriExposure();
##### 这是为了防止拍照时候由于6.0以上系统对文件路径的限制 
##### 然后在xml文件中加入  
    <com.wqj.selectfileandeditimage.selectpicture.view.SelectFileGridViewEx
        android:layout_margin="@dimen/dp10"
        android:id="@+id/pickFile"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
##### 在Activity中加入  
    private SelectFileGridViewEx mSelectFileGridViewEx;  
      mSelectFileGridViewEx = (SelectFileGridViewEx) findViewById(R.id.pickFile);//找到控件  
      mSelectFileGridViewEx.init(this, 4);//初始化并设置可选择文件个数  
      mSelectFileGridViewEx.setMenu(true, true, true, true, true, true);//设置可选择文件的菜单，具体类型可查看源文件，也可以不设置默认全部类型  
##### 在onactivityresult中加入 
    mSelectFileGridViewEx.setIntent(requestCode, resultCode, data);
#### 还有6.0以上的设备记得在程序中动态授权 拍照，录音等的权限，否则程序会崩溃

## 感谢
https://github.com/siwangqishiq/ImageEditor-Android
