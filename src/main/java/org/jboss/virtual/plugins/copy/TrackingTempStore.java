/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.virtual.plugins.copy;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.util.collection.ConcurrentSet;
import org.jboss.util.file.Files;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.spi.TempStore;

/**
 * Track files temp store.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class TrackingTempStore implements TempStore
{
   private TempStore delegate;
   private Set<File> files;

   public TrackingTempStore(TempStore delegate)
   {
      if (delegate == null)
         throw new IllegalArgumentException("Null delegate");
      this.delegate = delegate;
      this.files = new ConcurrentSet<File>();
   }

   /**
    * Get files.
    *
    * @return the files
    */
   public Set<File> getFiles()
   {
      return Collections.unmodifiableSet(files);
   }

   public File createTempFolder(VirtualFile file)
   {
      File dir = delegate.createTempFolder(file);
      files.add(dir);
      return dir;
   }

   public File createTempFolder(String outerName, String innerName)
   {
      File dir = delegate.createTempFolder(outerName, innerName);
      files.add(dir);
      return dir;
   }

   public void clear()
   {
      Set<File> copy = new HashSet<File>(files);
      files.clear();
      for (File dir : copy)
         Files.delete(dir);
   }
}