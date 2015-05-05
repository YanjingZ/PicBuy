package team11.desiredish.TakePhoto;

import android.os.Environment;

import java.io.File;

/**
 * Created by Yanjing on 10/18/14.
 */
public class FroyoAlbumDirFactory extends AlbumStorageDirFactory{

    @Override
    public File getAlbumStorageDir(String albumName) {
        // TODO Auto-generated method stub
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }

}
