package org.klesun.lang;

import org.junit.Assert;
import org.junit.Test;

public class PhpStrTest
{
    @Test public void keepsUnknownSequences()
    {
        Assert.assertEquals("App\\users\\Foo", PhpStr.unescape("App\\users\\Foo"));
        Assert.assertEquals("C:\\Users\\x", PhpStr.unescape("C:\\Users\\x"));
        Assert.assertEquals("\\u12", PhpStr.unescape("\\u12"));
        Assert.assertEquals("\\u{zz}", PhpStr.unescape("\\u{zz}"));
        Assert.assertEquals("tail\\", PhpStr.unescape("tail\\"));
    }

    @Test public void unescapesPhpSequences()
    {
        Assert.assertEquals("a\nb\t$\"\\", PhpStr.unescape("a\\nb\\t\\$\\\"\\\\"));
        Assert.assertEquals("A", PhpStr.unescape("\\x41"));
        Assert.assertEquals("A", PhpStr.unescape("\\101"));
        Assert.assertEquals("\uD83D\uDE00", PhpStr.unescape("\\u{1F600}"));
    }

    @Test public void escapesJs()
    {
        Assert.assertEquals("it\\'s\\n\\\\", PhpStr.escapeJs("it's\n\\"));
    }
}
