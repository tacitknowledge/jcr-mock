package com.tacitknowledge.jcr.testing.impl;

import com.tacitknowledge.jcr.testing.JcrRepositoryManager;
import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.TransientRepository;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Convenience class to create and manage a JCR transient repository
 *
 * @author Daniel Valencia (daniel@tacitknowledge.com)
 */
public class TransientRepositoryManager implements JcrRepositoryManager
{
    private String repositoryConfigPath;

    private String repositoryDirectoryPath;

    private String repositoryUser;

    private String repositoryPassword;

    private Repository repository;

    private Session session;

    private File tmpFile;

    private NodeTypeManager nodeTypeManager;

    /**
     * @param repositoryConfigPath - Path of the repository configuration file
     * @param repositoryDirectoryPath - Path of the repository directory
     * @param userName - Default admin username for the transient repository
     * @param password - Default admin password for the transient repository
     */
    public TransientRepositoryManager(String repositoryConfigPath, String repositoryDirectoryPath, String userName,
            String password)
    {
        this.repositoryConfigPath = repositoryConfigPath;
        this.repositoryDirectoryPath = repositoryDirectoryPath;
        this.repositoryUser = userName;
        this.repositoryPassword = password;
    }

    /**
     * Creates a file-based JCR transient repository
     * @return TransientRepository
     * @throws IOException If an IO error happens
     */
    public Repository startRepository() throws IOException
    {
        if(repository == null){

            // A temporary file creation is needed along with the jackrabbit transient repo configuration because it's not possible
            // to obtain a file directly from a JAR resource (i.e. if we get the resource path via getClass().getResource().getPath()
            // we get something like 'file://path/to/jar/myJar.jar!/jackrabbit/jackrabbit-transient.xml' (notice the Bang !).
            // We ARE able, however, to read the contents of it and hence, we can write it to a temporary file , which is
            // what we are doing.
            InputStream configStream = getRepositoryConfigurationAsStream();
            tmpFile = createTempFile(configStream);
            String repositoryPath = createResourceDir(repositoryDirectoryPath);
            repository = new TransientRepository(tmpFile, new File(repositoryPath));
        }
        return repository;
   }

    protected InputStream getRepositoryConfigurationAsStream()
    {
        return getClass().getResourceAsStream(repositoryConfigPath);
    }

    @Override
    public Session getSession() throws RepositoryException
    {
        if (session == null || !session.isLive())
        {
            session = repository.login(new SimpleCredentials(repositoryUser, repositoryPassword.toCharArray()));
        }

        return session;
    }

    @Override
    public void shutdownRepository()
    {
        if (tmpFile != null)
        {
            tmpFile.delete();
        }

        if (repository != null)
        {
            ((JackrabbitRepository) repository).shutdown();
            repository = null;
        }

    }

    @Override
    public NodeTypeManager getNodeTypeManager() throws RepositoryException
    {
        if (nodeTypeManager == null)
        {
            nodeTypeManager = getSession().getWorkspace().getNodeTypeManager();
        }

        return nodeTypeManager;
    }

    /**
     * Creates a valid node type manager instance
     * @param repositoryConfigPath Path for the repository configuration file
     * @param repositoryDirectoryPath Path for the repository directory
     * @param userName Repository username
     * @param password Repository password
     * @return NodeTypeManager
     * @throws IOException If a IO error happens
     * @throws RepositoryException If a repository error happens
     */
    public static NodeTypeManager createNodeTypeManager(String repositoryConfigPath, String repositoryDirectoryPath,
            String userName, String password) throws IOException, RepositoryException
    {
        TransientRepositoryManager runner = new TransientRepositoryManager(repositoryConfigPath,
                repositoryDirectoryPath, userName, password);
        runner.startRepository();
        NodeTypeManager nodeTypeManager = runner.getNodeTypeManager();
        runner.shutdownRepository();
        return nodeTypeManager;
    }

    /**
     * Creates a valid NodeTypeManager instance with default configuration
     * @return NodeTypeManager
     * @throws IOException If a IO error happens
     * @throws RepositoryException If a repository error happens
     */
    public static NodeTypeManager createNodeTypeManager() throws RepositoryException, IOException
    {
        String repositoryConfigPath = "/jackrabbit/jackrabbit-transient.xml";
        String repositoryDirectoryPath = "/jackrabbit/repository";
        String userName = "admin";
        String password = "admin";

        return createNodeTypeManager(repositoryConfigPath, repositoryDirectoryPath, userName, password);
    }

    private File createTempFile(InputStream configStream) throws IOException
    {
        BufferedReader configReader = new BufferedReader(new InputStreamReader(configStream));

        File temporaryFile = File.createTempFile("jackrabbit-transient", ".xml");
        FileWriter configWriter = new FileWriter(temporaryFile);
        String line;

        while ((line = configReader.readLine()) != null)
        {
            configWriter.write(line);
        }

        configReader.close();
        configWriter.close();

        return temporaryFile;
    }

    private String createResourceDir(String path) throws IOException
    {
        String basePath = getClass().getResource("/").getPath();
        File filePath = new File(basePath + path);
        FileUtils.forceMkdir(filePath);
        return filePath.getPath();
    }
}
