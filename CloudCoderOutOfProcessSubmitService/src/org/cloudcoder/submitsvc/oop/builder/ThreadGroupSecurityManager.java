// CloudCoder - a web-based pedagogical programming environment
// Copyright (C) 2011, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011, David H. Hovemeyer <dhovemey@ycp.edu>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
package org.cloudcoder.submitsvc.oop.builder;

import java.security.Permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security Manager that we use for Java and Jython code.  
 * Each student test case is run in 
 * a separate thread, and all of those threads are part of the same ThreadGroup,
 * which is passed into the constructor of this class.
 * 
 * Any thread in the checkedThreadGroup is heavily restricted as far as what 
 * it may do.  This security model is very simple but is effective so far.
 * 
 * 
 * @author jaimespacco
 *
 */
public class ThreadGroupSecurityManager extends SecurityManager
{
    private static final Logger logger=LoggerFactory.getLogger(ThreadGroupSecurityManager.class);
    
    private ThreadGroup checkedThreadGroup;
    
    public ThreadGroupSecurityManager(ThreadGroup threadGroup) {
        super();
        this.checkedThreadGroup=threadGroup;
    }
    
    @Override
    public void checkAccess(Thread t) {
        if (isCheckedThreadGroup()) {
            throw new SecurityException("Cannot access Thread");
        }
    }
    
    @Override
    public void checkAccess(ThreadGroup g) {
        if (isCheckedThreadGroup()) {
            throw new SecurityException("Cannot access ThreadGroup");
        }
    }

    @Override
    public void checkPermission(Permission perm) {
        check(perm);
    }
    
    @Override
    public void checkPermission(Permission perm, Object context) {
        check(perm);
    }
    
    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkCreateClassLoader()
     */
    @Override
    public void checkCreateClassLoader() {
        if (isCheckedThreadGroup()) {
            throw new SecurityException("Cannot create classloader");
        }
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#getThreadGroup()
     */
    @Override
    public ThreadGroup getThreadGroup() {
        return super.getThreadGroup();
    }
    
    private boolean isCheckedThreadGroup() {
        ThreadGroup group=getThreadGroup();
        if (group==checkedThreadGroup) {
            return true;
        }
        return false;
    }
    
    private void check(Permission perm) {
        // allow reading the line separator
        if (perm.getName().equals("line.separator") && perm.getActions().contains("read")) {
            return;
        }
        if (perm.getName().equals("accessDeclaredMembers")) {
            return;
        }
        if (isCheckedThreadGroup()) {
            throw new SecurityException("Student code does not have permission to: " +perm.getName());
        }
        
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkExit(int)
     */
    @Override
    public void checkExit(int status) {
        if (isCheckedThreadGroup()) {
            throw new SecurityException("Student code should not call System.exit(int i)");
        }
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkExec(java.lang.String)
     */
    @Override
    public void checkExec(String cmd) {
        if (isCheckedThreadGroup()) {
            throw new SecurityException("Student code cannot execute commands");
        }
    }

    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkPackageDefinition(java.lang.String)
     */
//    @Override
//    public void checkPackageDefinition(String pkg) {
//        System.out.println("checkPackageDefinition");
//        if (pkg.equals("edu.ycp.cs.netcoder.server.compilers")) {
//            return;
//        }
//    }
    
}
