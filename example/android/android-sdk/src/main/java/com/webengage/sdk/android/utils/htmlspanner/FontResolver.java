package com.webengage.sdk.android.utils.htmlspanner;

/**
 * Interface for font-resolving components.
 */
public interface FontResolver {

    FontFamily getDefaultFont();

    FontFamily getSansSerifFont();

    FontFamily getSerifFont();

    FontFamily getMonoSpaceFont();

    FontFamily getFont( String name );

}
