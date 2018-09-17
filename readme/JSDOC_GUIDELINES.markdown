# JSDoc Guidelines

Please follow these guidelines when submitting JSDoc contributions.

The sections [Class Comments](#class-comments) and 
[Method Comments](#method-comments) provide you with simple instructions on how 
to describe JavaScript classes and methods. Subsequent sections guide you in 
describing the
[most important tips](#top-tips) for writing Javadoc,
[formatting and building JSDoc](#formatting-and-building-jsdoc), and
[submitting your contributions](#submitting-jsdoc). This article covers the
main points and most important rules to follow. If you're interested in an
extensive amount of examples and more advanced information, visit the
[Advanced Javadoc Guidelines](ADVANCED_JAVADOC_GUIDELINES.markdown).

## Class Comments

The following information should be present in the following order in the class
comment:

- Initial class description (paragraph/sentence)
    - **First sentence** - Should describe the class clearly and concisely.
		(**required**)
    - **Followup sentences** - Support the first sentence with important points
	  about the class. (optional)
- Detailed class description (additional paragraph(s))
    - Provide more information on the class's purpose, abilities, and general
		 role. For some classes (simple utility classes for instance) this
		 additional information is not necessary if the initial paragraph provides
		 an adequate description.
    - Usage examples or `@link` tags to where the class can be seen in use.<!-- Not sure about this -->
- [@see tags](http://usejsdoc.org/tags-see.html) to other closely
  related classes whose JSDocs gives the reader a clearer picture of the
  purpose of this class.
- [@since tags](http://usejsdoc.org/tags-since.html). (as applicable)<!-- No examples of this in Portal -->
- [@deprecated tags](http://usejsdoc.org/tags-deprecated.html).
  (as applicable)

For more detailed information and examples for writing class descriptions, see
the
[Class Descriptions](ADVANCED_JAVADOC_GUIDELINES.markdown#class-descriptions)
section. A simple example class comment is provided below:

Example (class comment):

```JS
/**
 * Represents an example class. If a basic description requires more than one
 * sentence, include it in the first paragraph.
 *
 * <p>
 * Example of a second paragraph. Note the blank line between the first <p> tag
 * and the end of the first paragraph.
 * </p>
 *
 * @see {@link BigExample|BigExample}
 */
public class Example {
    ...
}
```

## Method Comments

The following information should be present in the JSDoc comment on each
method:

- A short, one sentence description of the method.
- Additional sentences and/or paragraphs providing more information on the
  method's purpose and function. Any information the user of the method would
	find useful should be included here, including special requirements,
	circumstances where it should/should not be used, etc. For many methods, this
	additional explanation is not necessary, particularly for getters and setters.
- Usage examples if necessary and possible. Trivial methods don't need examples,
  and if an example would be extremely large, simply *@link* to a place the
	method is used.
- If the method is only used in one or two places, *@link* to the methods it is
  called from. This helps later developers to understand its role in Liferay.
- No need to mention matching a company ID parameter. It should be understood
  and is not worth cluttering the description.

The following information should always be present in the following order in the 
JSDoc tags for the method:

- [@param](http://usejsdoc.org/tags-param.html) - The method parameters, in 
  order, with descriptions.
- [@return](http://usejsdoc.org/tags-returns.html) - All possible
  return values, including `null`. If the method is void, do not include this.
- [@inheritdoc](http://usejsdoc.org/tags-inheritdoc.html) - (as applicable)<!-- Not working -->
- [@throws](http://usejsdoc.org/tags-throws.html) - The
  exceptions the method can throw, in order, with explanations of what would
  trigger them. <!-- No examples of this in Portal -->
- [@see](http://usejsdoc.org/tags-see.html) - (as applicable)
- [@since](http://usejsdoc.org/tags-since.html) - (as applicable)<!-- No examples of this in Portal -->
- [@deprecated](http://usejsdoc.org/tags-deprecated.html) - (as applicable)

For more detailed information and examples for writing method descriptions, see
the
[Method Descriptions](ADVANCED_JAVADOC_GUIDELINES.markdown#method-descriptions)
section. A simple method description is provided below:

Example (method comments):

```JS
/**
 * Spawns a webworker to process the image in a different thread.
 * @param  {Object} message The image and brightness value.
 * @return {CancellablePromise} A promise that resolves when the webworker
 * finishes processing the image.
 */

spawnWorker_(message) {
  return new CancellablePromise((resolve, reject) => {
    let workerURI = this.modulePath + '/BrightnessWorker.js';
    let processWorker = new Worker(workerURI);

    processWorker.onmessage = (event) => resolve(event.data);
    processWorker.postMessage(message);
  });
}
```

## Top Tips

1.  If you're new to JSDoc, familiarize yourself with the
    [official JSDOC guide](http://usejsdoc.org/index.html).
2.  Starting with an *action word*, describe what each [class](#class-comments)
    and [method](#method-comments) does.
3.  Avoid just restating the class or method name (e.g., Avoid
    *updateLocalization(...)* &rarr; *Updates the localization* **OR** *@param
    key the key*).
4.  Describe the most important details in the first sentence.
5.  Include all relevant tags
    ([@param](http://usejsdoc.org/tags-param.html),
    [@return](http://usejsdoc.org/tags-returns.html), etc.) for
    each method; without them, the method JSDoc is incomplete.
6.  Start a method description with *Returns*, if the method returns a value.
7.  Don't explicitly refer to collections in descriptions (e.g.,
    *list of articles*). Use, instead, the plural (e.g., *the articles*) or *all
    the* (e.g., *all the articles*).
8.  When referring to another class, try to either link using 
    *{@link class|class}* (e.g., *{@link EventScreen|EventScreen}*) or refer to 
    the class in common terms (e.g., *Event Screen*). The first alternative
    provides direct access to the class's JSDoc, the second alternative makes
    for smooth reading.
9.  When referring to another method, either within the same class or another 
    class, link using *{@link class#method|method}* 
    (e.g., *{@link LiferayApp#getCacheExpirationTime|getCacheExpirationTime}*).
10.  Avoid referring explicitly to parameters by name; instead, refer to them in
    general terms (e.g., *class name ID* instead of *<code>classNameId</code>*).
11. Begin boolean parameter descriptions with *whether*. See example below:

    ```JS
    /**
     * @param {boolean} renderData whether to render the data
     */ 
    ```

12. Punctuate every class and method description (sentence or phrase) with a
    period.
13. Only punctuate an initial description (sentence or phrase) for a method tag
    [@param](http://usejsdoc.org/tags-param.html),
    [@return](http://usejsdoc.org/tags-returns.html), or
    [@throws](http://usejsdoc.org/tags-throws.html) if it's
    followed by a sentence(s). Write follow-up text in complete sentences.
14. Describe exceptions (e.g.,
	  [@throws](http://usejsdoc.org/tags-throws.html)) in past
    tense.
15. Don't wrap the first paragraph of a JSDoc comment with paragraph tags. Do
    wrap subsequent paragraphs with paragraph tags (e.g.,
    `<p>second paragraph</p>`).
16. Conform JSDoc to 80 columns, if possible.

Terrific! You're off to a great start to writing JSDoc. The following sections
describe the most important aspects of writing STATE object comments and STATE 
property comments, using the JSDoc Formatter, and submitting your contributions.

## State Object Comments

Each class may contain STATE objects that contain properties related to the 
component's render. The following information should be present in the JSDoc 
comment on each STATE object:

- A short, one sentence description of the STATE object.

The following information should always be present in the JSDoc tags for the 
STATE object:

- [@type](http://usejsdoc.org/tags-type.html) - Specifies the type that the 
STATE object contains.
- [@static](http://usejsdoc.org/tags-static.html) - Specifies that the STATE 
object is contained within the parent and can be accessed without instantiating 
the parent.

```JS
/**
 * State definition.
 * @type {!Object}
 * @static
 */
FragmentPreview.STATE = {
  ...
}
```

### STATE Properties

Each STATE Object contains properties that define settings for the STATE object. 
The following information should always be present:

- A short, one sentence description of the STATE property.

The following information should always be present in the JSDoc tags for the 
STATE property:

- [@default](ADVANCED_JAVADOC_GUIDELINES.markdown#param-tags) - The default 
value for the property. If not applicable, set it as `undefined`.
- [@instance](http://usejsdoc.org/tags-instance.html) - Marks the property as an 
instance member of the parent.
- [@memberOf](http://usejsdoc.org/tags-memberof.html) - Marks the property as a 
member of the parent.
- [@protected](http://usejsdoc.org/tags-protected.html) - Marks the property as 
protected, meaning it should only be used with the current module.
- [@type](http://usejsdoc.org/tags-type.html) - Specifies the type that the 
property may contain.

Example (STATE property comments):

```JS
/**
 * Flag that checks if the preview content is loading.
 * @default false
 * @instance
 * @memberOf FragmentPreview
 * @protected
 * @type {boolean}
 */
 
_loading: Config.bool()
  .internal()
  .value(false),
```


## Formatting and Building JSDoc

Before committing any new or modified JSDocs, run `../gradlew npmRunFormat` on 
your code first! This will automatically wrap your comments to the proper width,
format html tags, and line up JSDoc tags. 

1. Run the JSDoc Formatter.

    To format JSDoc in a module under `liferay-portal/modules/apps`, execute 
    this in the module's root folder:

        gradlew npmRunFormat

    The optional arguments you can pass are:<!-- Are there optional settings? -->

    - `-DformatJavadoc.limit="SomeClassName1,SomeClassName2,com.liferay.portal.**` -
    Runs the formatter on more than one class/package. Limits must be wrapped in
    double quotes. This can be combined with the following two options, or
    completely removed (which runs the formatter on the entire code base).
    - `-DformatJavadoc.init=true` - Inserts place holders for all comment
		elements.
    - `-DformatJavadoc.update=true` - Inserts place holders for comment elements
    that are not up to date with the current method signatures. For example, the
    formatter will add comment placeholders for parameters that are in method
    signatures but not present in the Javadoc comments for those methods.
    - `-DformatJavadoc.generate.xml` - Generates an XML document of the Javadoc
		that shows for a module's web services API page.

    To format Javadoc in a class you've edited in a Liferay Portal Core module
    (e.g., in `portal-kernel`, `portal-impl`, etc), run:

		    ant format-javadoc -Dlimit=SomeClassName

    There are several other alternative options for invoking basic Javadoc
    formatting and updates.

    - `-Dlimit="SomeClassName1,SomeClassName2,com.liferay.portal.**"` - Runs the
    formatter on more than one class/package. Limits must be wrapped in double
	  quotes. This can be combined with the following two options, or completely
	  removed (which runs the formatter on the entire code base).
    - `-Dinit=true` - Inserts place holders for all comment elements.
    - `-Dupdate=true`- Inserts place holders for comment elements that are not
    up to date with the current method signatures. For example, the formatter
    will add comment placeholders for parameters that are in method signatures
    but not present in the Javadoc comments for those methods.

3. Building JSDoc (optional)

    To optionally build a module's JSDoc HTML to the module's 
    `build/docs/jsdoc/module-name/version` folder to see what it looks like, 
    execute this:

        gradle jsdoc

    Open the generated `index.html` file to view the module's JSDoc.
    
    +$$$
    
    **Note:** This task can take a few minutes to generate the JSDoc as it 
    downloads Node as part of the process.
    
    $$$
     
    JSDoc HTML is only generated for modules that use the `.es.js` extension. 

## Submitting JSDoc

For more information on submitting JSDoc, see the
[JSDoc Submission Process](JSDOC_SUBMISSION_PROCESS.markdown) article.

To request **re-adding** JSDoc or comments that have been removed from a file,
please open an [LRDOCS](https://issues.liferay.com/browse/LRDOCS) JIRA ticket:

- **Issue Type:** API
- **Summary:** Re-add JSDoc for .... (name or prefix of class/file)
- **Component:** Area the JSDoc pertains to
- **Affected Release:** 7.1.x, 7.0.x, 6.2.x, 6.1.x
- **Description:** Include the *commit number* of the original JSDoc commit
  and/or the JSDoc removal commit
