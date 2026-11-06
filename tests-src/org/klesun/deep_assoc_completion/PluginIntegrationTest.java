package org.klesun.deep_assoc_completion;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction;
import com.intellij.json.JsonFileType;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.klesun.deep_assoc_completion.go_to_decl_providers.MainGoToDecl;

import java.util.List;

public class PluginIntegrationTest extends BasePlatformTestCase
{
    private List<String> complete(String php)
    {
        myFixture.configureByText("test.php", php);
        myFixture.completeBasic();
        List<String> strings = myFixture.getLookupElementStrings();
        return strings == null ? List.of() : strings;
    }

    public void testKeysFromLiteral()
    {
        List<String> keys = complete("<?php\n$arr = ['foo' => 1, 'bar' => 2];\n$arr['<caret>'];\n");
        assertTrue(keys.toString(), keys.contains("foo") && keys.contains("bar"));
    }

    public void testKeysFromOtherFunction()
    {
        List<String> keys = complete("<?php\nfunction mk() { return ['deep_key' => 1]; }\n$arr = mk();\n$arr['<caret>'];\n");
        assertTrue(keys.toString(), keys.contains("deep_key"));
    }

    /** the path of NoClassDefFoundError: MiscRes concatenation, with php namespace backslash-u */
    public void testConcatKeyWithBackslashU()
    {
        List<String> keys = complete("<?php\n$k = 'App\\users' . '\\Foo';\n$arr = [$k => 1];\n$arr['<caret>'];\n");
        assertTrue(keys.toString(), keys.contains("App\\users\\Foo"));
    }

    public void testImplodeKey()
    {
        List<String> keys = complete("<?php\n$k = implode('_', ['a', 'b']);\n$arr = [$k => 1];\n$arr['<caret>'];\n");
        assertTrue(keys.toString(), keys.contains("a_b"));
    }

    public void testGoToKeyDefinition()
    {
        myFixture.configureByText("test.php", "<?php\n$arr = ['foo' => 1];\necho $arr['fo<caret>o'];\n");
        PsiElement leaf = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        PsiElement[] targets = new MainGoToDecl().getGotoDeclarationTargets(leaf, myFixture.getCaretOffset(), myFixture.getEditor());
        assertNotNull(targets);
        assertTrue(targets.length > 0);
    }

    public void testGoToOnDynamicMemberDoesNotThrow()
    {
        myFixture.configureByText("test.php", "<?php\nclass A { public $x; }\n$a = new A();\n$n = 'x';\n$a->{$<caret>n};\n$a->$n;\n");
        PsiElement leaf = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        new MainGoToDecl().getGotoDeclarationTargets(leaf, myFixture.getCaretOffset(), myFixture.getEditor());
    }

    public void testJsonModuleVisible()
    {
        assertNotNull(JsonFileType.INSTANCE);
    }
}
