package uploader.mechanisms;

/**
 * Defines methods for uploading a file.  A file upload must start by calling
 * startUpload().  If that succeeds, then uploadNextChunk() should be called
 * until isUploadComplete() returns true.  cancelUpload() may be called anytime
 * after startUpload() and before isUploadComplete() returns true.
 *
 * After one file is uploaded, then the next one can be started by calling
 * startUpload() again.
 *
 * @author David Underhill
 */
public interface UploadMechanism {
    /**
     * Called once for each file which is to be uploaded.  Initiates the upload.
     * @return -1 on error; otherwise it returns the size of the file
     */
    public long startUpload(String fn);

    /**
     * Called until this method returns false or isUploadComplete() returns
     * true.  Should not be called if startUpload() has not yet been called or
     * if isUploadComplete() returns true.
     *
     * @param num_bytes_to_upload  the max number of bytes to upload with this chunk
     *
     * @return the number of bytes successfully uploaded, or -1 if the upload failed
     */
    public long uploadNextChunk(long num_bytes_to_upload);

    /**
     * Returns true if the upload is complete.
     */
    public boolean isUploadComplete();

    /**
     * Called to cancel the upload.  This does nothing if isUploadComplete()
     * returns true or if startUpload() or uploadNextChunk() returned an error
     * error code (false or -1 respectively).
     */
    public void cancelUpload();

    /**
     * Gets a human-readable string describing the most recent error, if any.
     * Returns null if there is no error.
     */
    public String getErrorText();
}
