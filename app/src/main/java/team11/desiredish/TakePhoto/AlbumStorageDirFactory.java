package team11.desiredish.TakePhoto;

import java.io.File;

/**
 * Created by Yanjing on 10/18/14.
 */
abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);

}
