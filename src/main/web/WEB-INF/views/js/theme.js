function userTheme(toggle  = false) {
    let userMode = localStorage.userThemeMode || 'auto';
    const osMode = !!window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches ? 'dark' : 'light';
    if(toggle) {
        switch(userMode) {
            case 'auto':
                userMode = 'dark'; break;
            case 'dark':
                userMode = 'light'; break;
            default:
                userMode = 'auto';
        }
        localStorage.userThemeMode = userMode;
    }
    console.log(`current mode : ${userMode}`);
    window.__THEME_MODE = userMode === 'auto' ? osMode : userMode;
    document.getElementsByTagName('html')[0].classList[window.__THEME_MODE === 'dark' ? 'add' : 'remove']('darkmode');
}


if (!!window.matchMedia) {
    ['light', 'dark'].forEach(mode => {
        window.matchMedia(`(prefers-color-scheme: ${mode})`).addListener(e => {
            if(!!e.matches) {
        userTheme();
    }
});
});
}

userTheme();