# ImagePicker Android Library

android library for imagepicker

#### Example

### Adding Library to Project

### Implementation

```
ImagePicker.with(this)                      
            .start()
```

```
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
            //adapter!!.setData(images)
        }
    }
```
#### Optional

Options | Use | Default
------------ | ------------- | -------------
setFolderMode(boolean) | Images show by folder wise | false
setCameraOnly(boolean) | It only shows camera for click | false
setFolderTitle(String) | Title for ImagePicker | Albums
setShowCamera(boolean) | it shows camera option to click new pic | false
setMultipleMode(boolean) | select multiple images | false
setSelectedImages(List<Image>) | set selected images | null
setMaxSize(int) | set maximum images to select | Maximum size
setBackgroundColor(String) | set background color | 
setRequestCode(int) | request code | 100
setKeepScreenOn(boolean) | keep screen on | false
setSavePath(String) | Image Save path |
setDoneTitle(String) | Done button text | Done
setAlwaysShowDoneButton(boolean) | Done button visibilty | false

 