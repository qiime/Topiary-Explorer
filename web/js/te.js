function showFeatures()
{
    document.getElementById("features").className = "visible";
    document.getElementById("about").className = "hidden";
    document.getElementById("download").className = "hidden";
    document.getElementById("screenshots").className = "hidden";
    document.getElementById("tutorial").className = "hidden";
    document.getElementById("bugfeature").className = "hidden";
}

function showAbout()
{
    document.getElementById("features").className = "hidden";
    document.getElementById("about").className = "visible";
    document.getElementById("download").className = "hidden";
    document.getElementById("screenshots").className = "hidden";
    document.getElementById("tutorial").className = "hidden";
    document.getElementById("bugfeature").className = "hidden";
}

function showDownload()
{
    document.getElementById("features").className = "hidden";
    document.getElementById("about").className = "hidden";
    document.getElementById("download").className = "visible";
    document.getElementById("screenshots").className = "hidden";
    document.getElementById("tutorial").className = "hidden";
    document.getElementById("bugfeature").className = "hidden";
}

function showScreenshots()
{
    document.getElementById("features").className = "hidden";
    document.getElementById("about").className = "hidden";
    document.getElementById("download").className = "hidden";
    document.getElementById("screenshots").className = "visible";
    document.getElementById("tutorial").className = "hidden";
    document.getElementById("bugfeature").className = "hidden";
}

function showTutorial()
{
    document.getElementById("features").className = "hidden";
    document.getElementById("about").className = "hidden";
    document.getElementById("download").className = "hidden";
    document.getElementById("screenshots").className = "hidden";
    document.getElementById("tutorial").className = "visible";
    document.getElementById("bugfeature").className = "hidden";
}

function showBugFeature()
{
    document.getElementById("features").className = "hidden";
    document.getElementById("about").className = "hidden";
    document.getElementById("download").className = "hidden";
    document.getElementById("screenshots").className = "hidden";
    document.getElementById("tutorial").className = "hidden";
    document.getElementById("bugfeature").className = "visible";
}