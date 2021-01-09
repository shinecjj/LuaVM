/*******************************************************************************
 * Copyright (c) 2009-2011 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package com.tencent.jse.vm2.lib.jse;

import com.tencent.jse.vm2.Globals;
import com.tencent.jse.vm2.LoadState;
import com.tencent.jse.vm2.LuaValue;
import com.tencent.jse.vm2.compiler.LuaC;
import com.tencent.jse.vm2.lib.Bit32Lib;
import com.tencent.jse.vm2.lib.CoroutineLib;
import com.tencent.jse.vm2.lib.DebugLib;
import com.tencent.jse.vm2.lib.PackageLib;
import com.tencent.jse.vm2.lib.ResourceFinder;
import com.tencent.jse.vm2.lib.StringLib;
import com.tencent.jse.vm2.lib.TableLib;

/** The {@link com.tencent.jse.vm2.lib.jse.JsePlatform} class is a convenience class to standardize
 * how globals tables are initialized for the JSE platform. 
 * <p>
 * It is used to allocate either a set of standard globals using 
 * {@link #standardGlobals()} or debug globals using {@link #debugGlobals()}
 * <p>
 * A simple example of initializing globals and using them from Java is:
 * <pre> {@code
 * Globals globals = JsePlatform.standardGlobals();
 * globals.get("print").call(LuaValue.valueOf("hello, world"));
 * } </pre>
 * <p>
 * Once globals are created, a simple way to load and run a script is:
 * <pre> {@code
 * globals.load( new FileInputStream("main.com.tencent.jse.lua"), "main.com.tencent.jse.lua" ).call();
 * } </pre>
 * <p>
 * although {@code require} could also be used: 
 * <pre> {@code
 * globals.get("require").call(LuaValue.valueOf("main"));
 * } </pre>
 * For this to succeed, the file "main.com.tencent.jse.lua" must be in the current directory or a resource.
 * See {@link com.tencent.jse.vm2.lib.jse.JseBaseLib} for details on finding scripts using {@link ResourceFinder}.
 * <p>
 * The standard globals will contain all standard libraries plus {@code luajava}:
 * <ul>
 * <li>{@link Globals}</li>
 * <li>{@link com.tencent.jse.vm2.lib.jse.JseBaseLib}</li>
 * <li>{@link PackageLib}</li>
 * <li>{@link Bit32Lib}</li>
 * <li>{@link TableLib}</li>
 * <li>{@link StringLib}</li>
 * <li>{@link CoroutineLib}</li>
 * <li>{@link com.tencent.jse.vm2.lib.jse.JseMathLib}</li>
 * <li>{@link com.tencent.jse.vm2.lib.jse.JseIoLib}</li>
 * <li>{@link com.tencent.jse.vm2.lib.jse.JseOsLib}</li>
 * <li>{@link com.tencent.jse.vm2.lib.jse.LuajavaLib}</li>
 * </ul>
 * In addition, the {@link LuaC} compiler is installed so com.tencent.jse.lua files may be loaded in their source form.
 * <p> 
 * The debug globals are simply the standard globals plus the {@code debug} library {@link DebugLib}.
 * <p>
 * The class ensures that initialization is done in the correct order.
 * 
 * @see Globals
 * @see com.tencent.jse.vm2.lib.jme.JmePlatform
 */
public class JsePlatform {

	/**
	 * Create a standard set of globals for JSE including all the libraries.
	 * 
	 * @return Table of globals initialized with the standard JSE libraries
	 * @see #debugGlobals()
	 * @see com.tencent.jse.vm2.lib.jse.JsePlatform
	 * @see com.tencent.jse.vm2.lib.jme.JmePlatform
	 */
	public static Globals standardGlobals() {
		Globals globals = new Globals();
		globals.load(new JseBaseLib());
		globals.load(new PackageLib());
		globals.load(new Bit32Lib());
		globals.load(new TableLib());
		globals.load(new StringLib());
		globals.load(new CoroutineLib());
		globals.load(new JseMathLib());
		globals.load(new JseIoLib());
		globals.load(new JseOsLib());
		globals.load(new LuajavaLib());
		LoadState.install(globals);
		LuaC.install(globals);
		return globals;		
	}

	/** Create standard globals including the {@link DebugLib} library.
	 * 
	 * @return Table of globals initialized with the standard JSE and debug libraries
	 * @see #standardGlobals()
	 * @see com.tencent.jse.vm2.lib.jse.JsePlatform
	 * @see com.tencent.jse.vm2.lib.jme.JmePlatform
	 * @see DebugLib
	 */
	public static Globals debugGlobals() {
		Globals globals = standardGlobals();
		globals.load(new DebugLib());
		return globals;
	}


	/** Simple wrapper for invoking a com.tencent.jse.lua function with command line arguments.
	 * The supplied function is first given a new Globals object, 
	 * then the program is run with arguments.
	 */
	public static void luaMain(LuaValue mainChunk, String[] args) {
		Globals g = standardGlobals();
		int n = args.length;
		LuaValue[] vargs = new LuaValue[args.length];
		for (int i = 0; i < n; ++i)
			vargs[i] = LuaValue.valueOf(args[i]);
		LuaValue arg = LuaValue.listOf(vargs);
		arg.set("n", n);
		g.set("arg", arg);
		mainChunk.initupvalue1(g);
		mainChunk.invoke(LuaValue.varargsOf(vargs));
	}
}