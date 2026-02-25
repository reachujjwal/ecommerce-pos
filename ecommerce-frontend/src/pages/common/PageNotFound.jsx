import React from 'react'

const PageNotFound = () => {
  return (
    <div className="h-screen flex flex-col justify-center items-center p-8 text-center bg-background">
      <h1 className="font-bold text-5xl text-foreground mb-4">404</h1>
      <p className="text-xl text-muted-foreground">Page Not Found</p>
      <button
        onClick={() => window.history.back()}
        className="mt-8 text-primary hover:underline font-medium"
      >
        ← Go Back
      </button>
    </div>
  )
}

export default PageNotFound