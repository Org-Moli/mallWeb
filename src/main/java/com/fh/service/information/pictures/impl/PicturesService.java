
package com.fh.service.information.pictures.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import javax.annotation.Resource;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.fh.exception.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yq.dao.DaoSupport;
import org.change.entity.Page;
import com.fh.service.information.pictures.PicturesManager;
import org.change.util.PageData;


/**
 * 图片管理
 * 修改时间：2015.11.2
 */
@Service("picturesService")
public class PicturesService implements PicturesManager {

    private static final Logger LOGGER = Logger.getLogger(PicturesService.class);

    @Value("#{ossProperty['oss.apikey']}")
    private String ACCESS_KEY_ID;

    @Value("#{ossProperty['oss.apisecret']}")
    private String ACCESS_KEY_SECRET;

    @Value("#{ossProperty['oss.endpoint']}")
    private String END_POINT;

    @Value("#{ossProperty['oss.bucket.file.domain']}")
    private String domainName_file;

    @Value("#{ossProperty['oss.bucket.www.domain']}")
    private String domainName_www;

    @Value("#{ossProperty['oss.bucket.pic.domain']}")
    private String domainName_pic;

    @Value("#{ossProperty['oss.bucket.www']}")
    private String BUCKET_NAME_WWW;

    @Value("#{ossProperty['oss.bucket.file']}")
    private String BUCKET_NAME_FILE;

    @Value("#{ossProperty['oss.bucket.pic']}")
    private String BUCKET_NAME_PIC;

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    /**
     * 列表
     *
     * @param page
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<PageData> list(Page page) throws Exception
    {
        return (List<PageData>) dao.findForList("PicturesMapper.datalistPage", page);
    }

    /**
     * 新增
     *
     * @param pd
     * @throws Exception
     */
    public void save(PageData pd) throws Exception
    {
        dao.save("PicturesMapper.save", pd);
    }

    /**
     * 删除
     *
     * @param pd
     * @throws Exception
     */
    public void delete(PageData pd) throws Exception
    {
        dao.delete("PicturesMapper.delete", pd);
    }

    /**
     * 修改
     *
     * @param pd
     * @throws Exception
     */
    public void edit(PageData pd) throws Exception
    {
        dao.update("PicturesMapper.edit", pd);
    }

    /**
     * 通过id获取数据
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public PageData findById(PageData pd) throws Exception
    {
        return (PageData) dao.findForObject("PicturesMapper.findById", pd);
    }

    /**
     * 批量删除
     *
     * @param ArrayDATA_IDS
     * @throws Exception
     */
    public void deleteAll(String[] ArrayDATA_IDS) throws Exception
    {
        dao.delete("PicturesMapper.deleteAll", ArrayDATA_IDS);
    }

    /**
     * 批量获取
     *
     * @param ArrayDATA_IDS
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<PageData> getAllById(String[] ArrayDATA_IDS) throws Exception
    {
        return (List<PageData>) dao.findForList("PicturesMapper.getAllById", ArrayDATA_IDS);
    }

    /**
     * 删除图片
     *
     * @param pd
     * @throws Exception
     */
    public void delTp(PageData pd) throws Exception
    {
        dao.update("PicturesMapper.delTp", pd);
    }

    @Override
    public Map<String, Object> uploadOss(PageData pageData)
    {
        Map<String, Object> resultMap = new HashMap<>();
        String bucketKey = pageData.getString("bucketKey");
        if (StringUtils.isBlank(bucketKey))
        {
            resultMap.put("success", false);
            resultMap.put("errMsg", "bucketKey不能为空");
            return resultMap;
        }
        String bucket = null;
        if ("oss.bucket.www".equalsIgnoreCase(bucketKey))
        {
            bucket = BUCKET_NAME_WWW;
        } else if ("oss.bucket.file".equalsIgnoreCase(bucketKey))
        {
            bucket = BUCKET_NAME_FILE;
        } else if ("oss.bucket.pic".equalsIgnoreCase(bucketKey))
        {
            bucket = BUCKET_NAME_PIC;
        }
        if (StringUtils.isBlank(bucket))
        {
            resultMap.put("success", false);
            resultMap.put("errMsg", "bucket不能为空");
            return resultMap;
        }

        String domainName;
        if (BUCKET_NAME_FILE.equals(bucket))
        {
            domainName = domainName_file;
        } else if (BUCKET_NAME_WWW.equals(bucket))
        {
            domainName = domainName_www;
        } else if (BUCKET_NAME_PIC.equals(bucket))
        {
            domainName = domainName_pic;
        } else
        {
            resultMap.put("success", false);
            resultMap.put("errMsg", "目标bucket[" + bucket + "]不存在");
            return resultMap;
        }
        String ossPath = pageData.getString("ossPath");
        if (ossPath.startsWith("/"))
        {
            ossPath = ossPath.substring(1);
        }
        if (ossPath.length() > 0 && !ossPath.endsWith("/"))
        {
            ossPath = ossPath + "/";
        }
        // 1 扫描路径
        String targetPathStr = pageData.getString("targetPath");
        if (StringUtils.isBlank(targetPathStr))
        {
            resultMap.put("success", false);
            resultMap.put("errMsg", "目标路径[" + targetPathStr + "]不存在");
            return resultMap;
        }
        File targetPath = new File(targetPathStr);
        if (!targetPath.exists())
        {
            resultMap.put("success", false);
            resultMap.put("errMsg", "目标路径文件[" + targetPath.getAbsolutePath() + "]不存在");
            return resultMap;
        }
        LOGGER.debug("开始上传路径[" + targetPath.getAbsolutePath() + "]");

        List<File> todoFileList = new ArrayList<>();
        List<File> dirList = new ArrayList<>();
        Stack<File> todoDir = new Stack<>();
        // 2 如果是目录，则递归处理
        if (targetPath.isDirectory())
        {
            todoDir.push(targetPath);
            while (!todoDir.empty())
            {
                File dir = todoDir.pop();
                File[] subFiles = dir.listFiles();
                if (subFiles != null)
                {
                    for (File subFile : subFiles)
                    {
                        if (subFile.isDirectory())
                        {
                            todoDir.push(subFile);
                            dirList.add(subFile);
                        } else
                        {
                            todoFileList.add(subFile);
                        }
                    }
                }
            }
            LOGGER.debug("目标路径为目录，扫描后获得[" + todoFileList.size() + "]个文件");
        } else
        {
            todoFileList.add(targetPath);
            targetPathStr = targetPath.getParent();
        }
        // 3 逐个上传OSS
        // 3.1 首先确保OSS上存在对应目录
        for (File dir : dirList)
        {
            if (!isDirOrFileExist(dir, targetPathStr, ossPath, bucket))
            {
                createDir(dir, targetPathStr, ossPath, bucket);
            }
        }
        // 3.2 开始逐个上传文件
        int successCount = 0;
        int failCount = 0;
        Map<String, String> successMap = new HashMap<>();
        List<String> failList = new ArrayList<>();
        for (File file : todoFileList)
        {
            try
            {
                String key = this.uploadFile(file, targetPathStr, ossPath, bucket);
                successCount++;
                String url;
                if (domainName_file.equals(domainName))
                {
                    url = key;
                } else
                {
                    url = domainName + "/" + key;
                }
                successMap.put(file.getAbsolutePath(), url);
                LOGGER.debug("文件[" + file.getAbsolutePath() + "]已成功上传OSS，bucket[" + bucket + "]");

            } catch (Exception e)
            {
                LOGGER.error("文件[" + file.getAbsolutePath() + "]上传OSS失败，bucket[" + bucket + "]", e);
                failCount++;
                failList.add(file.getAbsolutePath());
            }
        }
        resultMap.put("success", true);
        resultMap.put("successCount", successCount);
        resultMap.put("failCount", failCount);
        resultMap.put("successMap", successMap);
        resultMap.put("failList", failList);
        return resultMap;
    }

    boolean isDirOrFileExist(File localFile, String relativePath, String ossPath, String bucketName)
    {
        if (!relativePath.endsWith("/"))
        {
            relativePath += "/";
        }

        String key = localFile.getAbsolutePath().substring(relativePath.length());
        if (localFile.isDirectory() && !key.endsWith("/"))
        {
            key += "/";
        }
        if (key.startsWith("/"))
        {
            key = key.substring(1);
        }
        key = ossPath + key;
        try
        {
            OSSClient client = new OSSClient(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            client.getObjectMetadata(bucketName, key);
            return true;
        } catch (OSSException ose)
        {
            if (ose.getErrorCode().equals(OSSErrorCode.NO_SUCH_KEY))
            {
                return false;
            }
            throw ose;
        }
    }

    void createDir(File dir, String relativePath, String ossPath, String bucketName)
    {
        if (!relativePath.endsWith("/"))
        {
            relativePath += "/";
        }
        if (dir.getAbsolutePath().startsWith(relativePath))
        {
            String key = dir.getAbsolutePath().substring(relativePath.length());
            if (!key.endsWith("/"))
            {
                key += "/";
            }
            OSSClient client = new OSSClient(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            client.putObject(bucketName, ossPath + key, new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
        } else
        {
            throw new ServiceException(-1, "目标文件与指定的相对路径不符");
        }
    }

    String uploadFile(File file, String relativePath, String ossPath, String bucketName)
    {
        if (!relativePath.endsWith("/"))
        {
            relativePath += "/";
        }
        String key = file.getAbsolutePath().substring(relativePath.length());
        if (key.startsWith("/"))
        {
            key = key.substring(1);
        }
        key = ossPath + key;
        OSSClient client = new OSSClient(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());
        if (file.getName().toLowerCase().endsWith(".pdf"))
        {
            metadata.setHeader("Content-Type", "application/pdf");
        }

        try
        {
            client.putObject(bucketName, key, new FileInputStream(file), metadata);
            return key;
        } catch (FileNotFoundException e)
        {
            throw new ServiceException(-2, "要上传的文件[" + file.getAbsolutePath() + "]不存在");
        }
    }

}

