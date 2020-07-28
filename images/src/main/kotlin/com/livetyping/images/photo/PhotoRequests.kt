package com.livetyping.images.photo

import com.livetyping.images.photo.filecreator.*


/**
 * crete file in data/data/{applicationId}/files
 */
class PhotoRequestDefaultPath(
    chooser: String? = null
) : PhotoRequest(chooser, DefaultFileCreator())

/**
 * crete file in data/data/{applicationId}/cache/additionalPath
 */
class PhotoRequestCachePath(
    attrName: String,
    additionalPath: String? = null,
    fileName: String = "file_in_cache_dir",
    chooser: String? = null,
    maxFileSize: Int? = null
) : PhotoRequest(chooser, CachePathCreator(attrName, additionalPath, fileName), maxFileSize)

/**
 * crete file in /storage/emulated/0/Android/data/{applicationId}/cache/additionalPath
 */
class PhotoRequestExternalCachePath(
    attrName: String,
    additionalPath: String? = null,
    fileName: String = "file_in_external_cache_dir",
    chooser: String? = null
) : PhotoRequest(chooser, ExternalCachePathCreator(attrName, additionalPath, fileName))

/**
 * crete file in storage/emulated/0/Android/{applicationId}/files/additionalPath
 */
class PhotoRequestExternalFilesPath(
    attrName: String,
    additionalPath: String? = null,
    fileName: String = "file_in_external_files_dir",
    chooser: String? = null
) : PhotoRequest(chooser, ExternalFilesPathCreator(attrName, additionalPath, fileName))

/**
 * crete file in storage/emulated/0/files/additionalPath
 */
class PhotoRequestExternalPath(
    attrName: String,
    additionalPath: String? = null,
    fileName: String = "file_in_external_dir",
    chooser: String? = null
) : PhotoRequest(chooser, ExternalPathCreator(attrName, additionalPath, fileName))

/**
 * crete file in data/data/{applicationId}/files/additionalPath
 */
class PhotoRequestFilesPath(
    attrName: String,
    additionalPath: String? = null,
    fileName: String = "file_in_app_files_dir",
    chooser: String? = null
) : PhotoRequest(chooser, FilesPathCreator(attrName, additionalPath, fileName))
