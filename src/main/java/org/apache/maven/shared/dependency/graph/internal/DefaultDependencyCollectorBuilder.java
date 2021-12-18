package org.apache.maven.shared.dependency.graph.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyCollectorBuilder;
import org.apache.maven.shared.dependency.graph.DependencyCollectorBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Default project dependency raw dependency collector API, providing an abstraction layer against Maven
 * 3.1+ particular Aether implementations.
 * 
 * @author Gabriel Belingueres
 * @since 3.1.0
 */
@Component( role = DependencyCollectorBuilder.class )
public class DefaultDependencyCollectorBuilder
    extends AbstractLogEnabled
    implements DependencyCollectorBuilder, Contextualizable
{
    protected PlexusContainer container;

    @Override
    public DependencyNode collectDependencyGraph( ProjectBuildingRequest buildingRequest, ArtifactFilter filter )
        throws DependencyCollectorBuilderException
    {
        try
        {

            DependencyCollectorBuilder effectiveGraphBuilder =
                    container.lookup( DependencyCollectorBuilder.class, "maven31" );

            if ( getLogger().isDebugEnabled() )
            {
                MavenProject project = buildingRequest.getProject();

                getLogger().debug( "building Maven 3.1+ RAW dependency tree for " + project.getId() + " with "
                    + effectiveGraphBuilder.getClass().getSimpleName() );
            }

            return effectiveGraphBuilder.collectDependencyGraph( buildingRequest, filter );
        }
        catch ( ComponentLookupException e )
        {
            throw new DependencyCollectorBuilderException( e.getMessage(), e );
        }
    }

    /**
     * Injects the Plexus content.
     *
     * @param context Plexus context to inject.
     * @throws ContextException if the PlexusContainer could not be located.
     */
    @Override
    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

}
