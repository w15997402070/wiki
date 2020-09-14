package java.lang;

import java.io.*;
import java.lang.reflect.Executable;
import java.lang.annotation.Annotation;
import java.security.AccessControlContext;
import java.util.Properties;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.Map;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.AllPermission;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;
import sun.nio.ch.Interruptible;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.SecurityConstants;
import sun.reflect.annotation.AnnotationType;

/**
 * The <code>System</code> class contains several useful class fields
 * and methods. It cannot be instantiated.
 * System不能被实例化
 *
 * @author  unascribed
 * @since   JDK1.0
 */
public final class System {

    /* register the natives via the static initializer.
     *
     * VM will invoke the initializeSystemClass method to complete
     * the initialization for this class separated from clinit.
     * Note that to use properties set by the VM, see the constraints
     * described in the initializeSystemClass method.
     */
    private static native void registerNatives();
    static {
        registerNatives();
    }

    /** Don't let anyone instantiate this class */
    private System() {
    }

    /**
     * 标准输入
     */
    public final static InputStream in = null;

    /**
     * 标准输出
     */
    public final static PrintStream out = null;

    public final static PrintStream err = null;

    /* 
      安全管理器
     */
    private static volatile SecurityManager security = null;

    public static void setIn(InputStream in) {
        checkIO();
        setIn0(in);
    }

    public static void setOut(PrintStream out) {
        checkIO();
        setOut0(out);
    }

    public static void setErr(PrintStream err) {
        checkIO();
        setErr0(err);
    }

    private static volatile Console cons = null;
      
    /**
     * //TODO 这里待了解
     */
     public static Console console() {
         if (cons == null) {
             synchronized (System.class) {
                 cons = sun.misc.SharedSecrets.getJavaIOAccess().console();
             }
         }
         return cons;
     }

    public static Channel inheritedChannel() throws IOException {
        return SelectorProvider.provider().inheritedChannel();
    }
   /**
    * 检查是否有权限
    */
    private static void checkIO() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("setIO"));
        }
    }

    private static native void setIn0(InputStream in);
    private static native void setOut0(PrintStream out);
    private static native void setErr0(PrintStream err);

    /**
     * 设置安全管理器
     */
    public static void setSecurityManager(final SecurityManager s) {
        try {
            s.checkPackageAccess("java.lang");
        } catch (Exception e) {
            // no-op
        }
        setSecurityManager0(s);
    }

    private static synchronized void setSecurityManager0(final SecurityManager s) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            // ask the currently installed security manager if we
            // can replace it.
            sm.checkPermission(new RuntimePermission
                                     ("setSecurityManager"));
        }

        if ((s != null) && (s.getClass().getClassLoader() != null)) {
            // New security manager class is not on bootstrap classpath.
            // Cause policy to get initialized before we install the new
            // security manager, in order to prevent infinite loops when
            // trying to initialize the policy (which usually involves
            // accessing some security and/or system properties, which in turn
            // calls the installed security manager's checkPermission method
            // which will loop infinitely if there is a non-system class
            // (in this case: the new security manager class) on the stack).
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    s.getClass().getProtectionDomain().implies
                        (SecurityConstants.ALL_PERMISSION);
                    return null;
                }
            });
        }

        security = s;
    }

    public static SecurityManager getSecurityManager() {
        return security;
    }

    /**
     * 获取毫秒级的时间戳(1970年1月1日0时起的毫秒数)
     */
    public static native long currentTimeMillis();

    /**
     * 获取纳秒，返回的可能是任意时间（主要用于衡量时间段）
     */
    public static native long nanoTime();

    public static native void arraycopy(Object src,  int  srcPos,
                                        Object dest, int destPos,
                                        int length);

    /**
     * 获取对象地址方法
     */
    public static native int identityHashCode(Object x);

    /**
     * 系统环境变量类
     * System properties. The following properties are guaranteed to be defined:
     * <dl>
     * <dt>java.version         <dd>Java version number
     * <dt>java.vendor          <dd>Java vendor specific string
     * <dt>java.vendor.url      <dd>Java vendor URL
     * <dt>java.home            <dd>Java installation directory
     * <dt>java.class.version   <dd>Java class version number
     * <dt>java.class.path      <dd>Java classpath
     * <dt>os.name              <dd>Operating System Name
     * <dt>os.arch              <dd>Operating System Architecture
     * <dt>os.version           <dd>Operating System Version
     * <dt>file.separator       <dd>File separator ("/" on Unix)
     * <dt>path.separator       <dd>Path separator (":" on Unix)
     * <dt>line.separator       <dd>Line separator ("\n" on Unix)
     * <dt>user.name            <dd>User account name
     * <dt>user.home            <dd>User home directory
     * <dt>user.dir             <dd>User's current working directory
     * </dl>
     */

    private static Properties props;
    private static native Properties initProperties(Properties props);

    public static Properties getProperties() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertiesAccess();
        }

        return props;
    }

    /**
     * Returns the system-dependent line separator string. 
     * <p>On UNIX systems, it returns {@code "\n"}; on Microsoft
     * Windows systems it returns {@code "\r\n"}.
     */
    public static String lineSeparator() {
        return lineSeparator;
    }

    private static String lineSeparator;

    public static void setProperties(Properties props) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertiesAccess();
        }
        if (props == null) {
            props = new Properties();
            initProperties(props);
        }
        System.props = props;
    }

    public static String getProperty(String key) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertyAccess(key);
        }

        return props.getProperty(key);
    }


    public static String getProperty(String key, String def) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertyAccess(key);
        }

        return props.getProperty(key, def);
    }

    public static String setProperty(String key, String value) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key,
                SecurityConstants.PROPERTY_WRITE_ACTION));
        }

        return (String) props.setProperty(key, value);
    }

    public static String clearProperty(String key) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key, "write"));
        }

        return (String) props.remove(key);
    }

    private static void checkKey(String key) {
        if (key == null) {
            throw new NullPointerException("key can't be null");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("key can't be empty");
        }
    }

    public static String getenv(String name) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getenv."+name));
        }

        return ProcessEnvironment.getenv(name);
    }

    public static java.util.Map<String,String> getenv() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getenv.*"));
        }

        return ProcessEnvironment.getenv();
    }

    public static void exit(int status) {
        Runtime.getRuntime().exit(status);
    }

    public static void gc() {
        Runtime.getRuntime().gc();
    }

    public static void runFinalization() {
        Runtime.getRuntime().runFinalization();
    }

     @Deprecated
    public static void runFinalizersOnExit(boolean value) {
        Runtime.runFinalizersOnExit(value);
    }

   @CallerSensitive
    public static void load(String filename) {
        Runtime.getRuntime().load0(Reflection.getCallerClass(), filename);
    }

    @CallerSensitive
    public static void loadLibrary(String libname) {
        Runtime.getRuntime().loadLibrary0(Reflection.getCallerClass(), libname);
    }

     public static native String mapLibraryName(String libname);

    /**
     * Create PrintStream for stdout/err based on encoding.
     */
    private static PrintStream newPrintStream(FileOutputStream fos, String enc) {
       if (enc != null) {
            try {
                return new PrintStream(new BufferedOutputStream(fos, 128), true, enc);
            } catch (UnsupportedEncodingException uee) {}
        }
        return new PrintStream(new BufferedOutputStream(fos, 128), true);
    }


    /**
     * Initialize the system class.  Called after thread initialization.
     */
    private static void initializeSystemClass() {


        props = new Properties();
        initProperties(props);  // initialized by the VM

        sun.misc.VM.saveAndRemoveProperties(props);

        lineSeparator = props.getProperty("line.separator");
        sun.misc.Version.init();

        FileInputStream fdIn = new FileInputStream(FileDescriptor.in);
        FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
        FileOutputStream fdErr = new FileOutputStream(FileDescriptor.err);
        setIn0(new BufferedInputStream(fdIn));
        setOut0(newPrintStream(fdOut, props.getProperty("sun.stdout.encoding")));
        setErr0(newPrintStream(fdErr, props.getProperty("sun.stderr.encoding")));

        // Load the zip library now in order to keep java.util.zip.ZipFile
        // from trying to use itself to load this library later.
        loadLibrary("zip");

        // Setup Java signal handlers for HUP, TERM, and INT (where available).
        Terminator.setup();

        // Initialize any miscellenous operating system settings that need to be
        // set for the class libraries. Currently this is no-op everywhere except
        // for Windows where the process-wide error mode is set before the java.io
        // classes are used.
        sun.misc.VM.initializeOSEnvironment();

        // The main thread is not added to its thread group in the same
        // way as other threads; we must do it ourselves here.
        Thread current = Thread.currentThread();
        current.getThreadGroup().add(current);

        // register shared secrets
        setJavaLangAccess();

        // Subsystems that are invoked during initialization can invoke
        // sun.misc.VM.isBooted() in order to avoid doing things that should
        // wait until the application class loader has been set up.
        // IMPORTANT: Ensure that this remains the last initialization action!
        sun.misc.VM.booted();
    }

    private static void setJavaLangAccess() {
        // Allow privileged classes outside of java.lang
        sun.misc.SharedSecrets.setJavaLangAccess(new sun.misc.JavaLangAccess(){
            public sun.reflect.ConstantPool getConstantPool(Class<?> klass) {
                return klass.getConstantPool();
            }
            public boolean casAnnotationType(Class<?> klass, AnnotationType oldType, AnnotationType newType) {
                return klass.casAnnotationType(oldType, newType);
            }
            public AnnotationType getAnnotationType(Class<?> klass) {
                return klass.getAnnotationType();
            }
            public Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap(Class<?> klass) {
                return klass.getDeclaredAnnotationMap();
            }
            public byte[] getRawClassAnnotations(Class<?> klass) {
                return klass.getRawAnnotations();
            }
            public byte[] getRawClassTypeAnnotations(Class<?> klass) {
                return klass.getRawTypeAnnotations();
            }
            public byte[] getRawExecutableTypeAnnotations(Executable executable) {
                return Class.getExecutableTypeAnnotationBytes(executable);
            }
            public <E extends Enum<E>>
                    E[] getEnumConstantsShared(Class<E> klass) {
                return klass.getEnumConstantsShared();
            }
            public void blockedOn(Thread t, Interruptible b) {
                t.blockedOn(b);
            }
            public void registerShutdownHook(int slot, boolean registerShutdownInProgress, Runnable hook) {
                Shutdown.add(slot, registerShutdownInProgress, hook);
            }
            public int getStackTraceDepth(Throwable t) {
                return t.getStackTraceDepth();
            }
            public StackTraceElement getStackTraceElement(Throwable t, int i) {
                return t.getStackTraceElement(i);
            }
            public String newStringUnsafe(char[] chars) {
                return new String(chars, true);
            }
            public Thread newThreadWithAcc(Runnable target, AccessControlContext acc) {
                return new Thread(target, acc);
            }
            public void invokeFinalize(Object o) throws Throwable {
                o.finalize();
            }
        });
    }
}
